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
        const type = row.querySelector('.event-type').value;
        const teamSelector = row.querySelector('.team-selector');
        const team = teamSelector ? teamSelector.value : null;

        if (type === 'GOAL') {
            if (team === 'LOCAL') homeScore++;
            else if (team === 'VISITOR') awayScore++;
        }
    });

    const displayHomeInput = document.getElementById('displayHomeScore');
    const displayAwayInput = document.getElementById('displayAwayScore');
    if (displayHomeInput) displayHomeInput.value = homeScore;
    if (displayAwayInput) displayAwayInput.value = awayScore;

    const scoreHomeDiv = document.getElementById('scoreHome');
    const scoreAwayDiv = document.getElementById('scoreAway');
    if (scoreHomeDiv) scoreHomeDiv.innerText = homeScore;
    if (scoreAwayDiv) scoreAwayDiv.innerText = awayScore;
}

function updateScoreLabels() {
    const homeSelect = document.getElementById('homeTeam');
    const awaySelect = document.getElementById('awayTeam');
    const homeBadge = document.getElementById('homeBadge');
    const awayBadge = document.getElementById('awayBadge');

    if (homeSelect?.selectedIndex > 0) {
        homeBadge.innerText = homeSelect.options[homeSelect.selectedIndex].text;
    }
    if (awaySelect?.selectedIndex > 0) {
        awayBadge.innerText = awaySelect.options[awaySelect.selectedIndex].text;
    }
}

/**
 * 3. ENVÍO DE DATOS AL SERVIDOR (Corregido)
 */
document.getElementById('createMatchForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    calculateScore();

    const formData = new URLSearchParams();
    
    // 1. Campos básicos del partido
    const matchId = document.getElementById('matchId')?.value;
    if (matchId) formData.append('id', matchId);

    formData.append('localTeam.id', document.getElementById('homeTeam').value);
    formData.append('visitorTeam.id', document.getElementById('awayTeam').value);
    formData.append('localGoals', document.getElementById('displayHomeScore').value);
    formData.append('visitorGoals', document.getElementById('displayAwayScore').value);
    formData.append('matchDate', document.getElementById('matchDate').value);
    formData.append('matchTime', document.getElementById('matchTime').value);
    formData.append('weather', document.getElementById('weather').value);

    // 2. Campos dinámicos de eventos (Indexados para Spring)
    document.querySelectorAll('.event-row').forEach((row, index) => {
        const type = row.querySelector('.event-type').value;
        const role = row.querySelector('.team-selector').value;
        const isSub = (type === 'SUBSTITUTION');
        
        // Obtenemos el ID del equipo real según el rol seleccionado
        const teamId = (role === 'LOCAL') 
            ? document.getElementById('homeTeam').value 
            : document.getElementById('awayTeam').value;

        formData.append(`events[${index}].minute`, row.querySelector('.event-min').value || 0);
        formData.append(`events[${index}].type`, type);
        formData.append(`events[${index}].team.id`, teamId); // Vinculación necesaria para la DB
        
        if (!isSub) {
            formData.append(`events[${index}].namePlayer`, row.querySelector('.event-player').value);
        } else {
            formData.append(`events[${index}].namePlayerIn`, row.querySelector('.event-in').value);
            formData.append(`events[${index}].namePlayerOut`, row.querySelector('.event-out').value);
        }
    });

    try {
        const response = await fetch('/match/save', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData.toString()
        });

        // Manejo de la redirección del controlador convencional
        if (response.redirected) {
            Swal.fire({
                title: '¡Guardado!',
                text: 'El partido y sus eventos se han registrado.',
                icon: 'success',
                timer: 1500,
                showConfirmButton: false
            }).then(() => {
                window.location.href = response.url;
            });
        } else {
            throw new Error("Error en el servidor");
        }
    } catch (err) {
        Swal.fire({
            title: 'Error',
            text: 'No se pudo guardar. Revisa que todos los campos obligatorios estén llenos.',
            icon: 'error'
        });
    }
});

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
    updateScoreLabels();
    calculateScore();

    const container = document.getElementById('eventsContainer');
    if(container && container.querySelectorAll('.event-row').length === 0) {
        addEventField();
    }
};