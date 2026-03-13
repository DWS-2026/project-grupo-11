/**
 * 1. GESTIÓN DINÁMICA DE EVENTOS
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
 * 2. LÓGICA DE MARCADOR EN TIEMPO REAL
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
 * 3. ENVÍO DE DATOS AL SERVIDOR
 */
const form = document.getElementById('createMatchForm') || document.getElementById('editMatchForm');

if (form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        calculateScore();

        const formData = new URLSearchParams();
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

        // Bucle de eventos corregido (Un solo bucle para evitar errores)
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
            const response = await fetch('/admin/match/save', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            if (response.ok) {
                // Verificamos si Swal existe, si no usamos alert normal
                if (typeof Swal !== 'undefined') {
                    Swal.fire({ title: '¡Guardado!', icon: 'success', timer: 1500, showConfirmButton: false })
                        .then(() => window.location.href = '/admin/ModifyMatch'); 
                } else {
                    alert("¡Partido guardado con éxito!");
                    window.location.href = '/admin/ModifyMatch';
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
 * 4. CONTROL DE VISIBILIDAD DE CAMPOS
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
 * 5. CARGA INICIAL
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

        // Esta parte es importante para cuando editas
        if (typeSelect.value) toggleEditSubFields(typeSelect);
    });
    calculateScore();
}