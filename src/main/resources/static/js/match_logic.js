/**
 * 1. DYNAMIC ADDITION AND REMOVAL OF EVENTS
 */
function addEventField() {
    const container = document.getElementById('eventsContainer');
    const template = document.getElementById('eventTemplate');
    if (!template) return;

    const clone = template.content.cloneNode(true);
    container.appendChild(clone);
    calculateScore();
}

function removeEvent(btn) {
    btn.closest('.event-row').remove();
    calculateScore();
}

/**
 * 2. DYNAMIC SCORE CALCULATION
 * This function iterates through all the event rows, checks if the event type is "GOAL",
 *  and if so, it increments the score for the corresponding team (home or away) based 
 * on the team selector. Finally, it updates the displayed scores in the form.
 */
function calculateScore() {
    let homeScore = 0;
    let awayScore = 0;

    document.querySelectorAll('.event-row').forEach(row => {
        const typeEl = row.querySelector('.event-type');
        const teamEl = row.querySelector('.team-selector');
        
        if (typeEl && teamEl && typeEl.value === 'GOAL') {
            if (teamEl.value === 'LOCAL') homeScore++;
            else if (teamEl.value === 'VISITOR') awayScore++;
        }
    });

    if (document.getElementById('displayHomeScore')) document.getElementById('displayHomeScore').value = homeScore;
    if (document.getElementById('displayAwayScore')) document.getElementById('displayAwayScore').value = awayScore;
    if (document.getElementById('scoreHome')) document.getElementById('scoreHome').innerText = homeScore;
    if (document.getElementById('scoreAway')) document.getElementById('scoreAway').innerText = awayScore;
}

/**
 * 3. FORM SUBMISSION LOGIC
 * This part handles the submission of both the create and edit match forms. It gathers all the form data, 
 * including the dynamically added events, and sends it to the server using fetch. The CSRF token is included 
 * for security, and after a successful save, it redirects the user back to the match list page.
 */
const form = document.getElementById('createMatchForm') || document.getElementById('editMatchForm');

if (form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        calculateScore();

        const formData = new URLSearchParams();
        const csrfToken = document.querySelector('input[name="_csrf"]').value;
        const matchId = document.getElementById('matchId')?.value;
        if (matchId && matchId !== "") {
            formData.append('id', matchId);
        }

        formData.append('localTeam.id', document.getElementById('homeTeam').value);
        formData.append('visitorTeam.id', document.getElementById('awayTeam').value);
        formData.append('localGoals', document.getElementById('displayHomeScore').value || 0);
        formData.append('visitorGoals', document.getElementById('displayAwayScore').value || 0);
        formData.append('matchDate', document.getElementById('matchDate').value);
        formData.append('matchTime', document.getElementById('matchTime').value);
        formData.append('weather', document.getElementById('weather').value);
        const stadiumInput = document.getElementById('stadium');
        formData.append('stadium', stadiumInput ? stadiumInput.value : "");
        formData.append('_csrf', csrfToken);

        // Events Loop: We iterate through all the event 
        // rows and append their data to the formData with the correct naming convention
        document.querySelectorAll('.event-row').forEach((row, index) => {
            const eventId = row.querySelector('.event-id')?.value;
            if (eventId && eventId !== "") {
                formData.append(`events[${index}].id`, eventId);
            }

            const type = row.querySelector('.event-type').value;
            const role = row.querySelector('.team-selector').value;
            const teamId = (role === 'LOCAL')
                ? document.getElementById('homeTeam').value
                : document.getElementById('awayTeam').value;

            formData.append(`events[${index}].minute`, row.querySelector('.event-min').value || 0);
            formData.append(`events[${index}].type`, type);
            formData.append(`events[${index}].team.id`, teamId);

            if (type !== 'SUBSTITUTION') {
                formData.append(`events[${index}].namePlayer`, row.querySelector('.event-player').value || "");
            } else {
                formData.append(`events[${index}].namePlayerIn`, row.querySelector('.event-in').value || "");
                formData.append(`events[${index}].namePlayerOut`, row.querySelector('.event-out').value || "");
            }
        });

        try {
            const response = await fetch('/admin/matches/match/save', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            if (response.ok) {
                // We check if Swal is available for a nicer alert, 
                // otherwise we fall back to a simple alert
                if (typeof Swal !== 'undefined') {
                    Swal.fire({ title: '¡Guardado!', icon: 'success', timer: 1500, showConfirmButton: false })
                        .then(() => window.location.href = '/admin/matches/modify-match'); 
                } else {
                    alert("¡Partido guardado con éxito!");
                    window.location.href = '/admin/matches/modify-match';
                }
            } else {
                throw new Error("Error en el servidor");
            }
        } catch (err) {
            if (typeof Swal !== 'undefined') {
                Swal.fire({ title: 'Error', text: 'No se pudo guardar el partido.', icon: 'error' });
            } else {
                alert("Error: No se pudo guardar el partido.");
            }
        }
    });
}

/**
 * 4. DYNAMIC SHOW/HIDE OF SUBSTITUTION FIELDS
 * This function is called whenever the event type select changes. If the selected type is "SUBSTITUTION",
 * it hides the regular player input and shows the substitution fields (player in and player out). 
 * If it's any other type, it does the opposite.
 */
function toggleEditSubFields(select) {
    const row = select.closest('.event-row');
    const playerInput = row.querySelector('.event-player');
    const subFields = row.querySelector('.sub-fields');

    if (select.value === 'SUBSTITUTION') {
        playerInput.classList.add('d-none');
        subFields.classList.remove('d-none');
        playerInput.removeAttribute('required');
    } else {
        playerInput.classList.remove('d-none');
        subFields.classList.add('d-none');
        playerInput.setAttribute('required', 'required');
    }
}

/**
 * 5. INITIALIZATION
 * When the page loads, we call syncSavedEvents to ensure that the form is in sync with any existing events (important for editing).
 * We also set up an event listener on the container that holds the events, so that whenever an event type or team selector changes, 
 * we recalculate the score in real time.
 */
window.onload = () => {
    syncSavedEvents();

    const container = document.getElementById('eventsContainer');
    if (container) {
        container.addEventListener('change', (e) => {
            if (e.target.classList.contains('event-type') || e.target.classList.contains('team-selector')) {
                calculateScore();
            }
        });
    }
};

function syncSavedEvents() {
    const homeTeamEl = document.getElementById('homeTeam');
    if(!homeTeamEl) return;
    
    const localTeamId = homeTeamEl.value;

    document.querySelectorAll('.event-row').forEach(row => {
        const typeSelect = row.querySelector('.event-type');
        const teamSelect = row.querySelector('.team-selector');

        // This part is crucial for the edit form: we need to set the team selector based on 
        // the saved event data,
        if (typeSelect.value) toggleEditSubFields(typeSelect);
    });
    calculateScore();
}