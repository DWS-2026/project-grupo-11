/**
 * 1. CARGA DE DATOS INICIALES
 */
async function fetchTeams() {
    try {
        const response = await fetch('/api/teams');
        const teams = await response.json();
        const homeSelect = document.getElementById('homeTeam');
        const awaySelect = document.getElementById('awayTeam');

        if (!homeSelect || !awaySelect) return;

        homeSelect.length = 1;
        awaySelect.length = 1;

        teams.forEach(team => {
            const option = `<option value="${team.id}">${team.name}</option>`;
            homeSelect.innerHTML += option;
            awaySelect.innerHTML += option;
        });
    } catch (error) {
        console.error("Error cargando equipos:", error);
    }
}

/**
 * 2. GESTIÓN DINÁMICA DE EVENTOS
 */
function addEventField() {
    const container = document.getElementById('eventsContainer');
    const template = document.getElementById('eventTemplate');
    if (!template) return;
    
    const clone = template.content.cloneNode(true);
    container.appendChild(clone);
}

function removeEvent(btn) {
    btn.closest('.event-row').remove();
    calculateScore();
}

/**
 * 3. LÓGICA DE MARCADOR EN TIEMPO REAL
 */
function calculateScore() {
    let homeScore = 0;
    let awayScore = 0;

    document.querySelectorAll('.event-row').forEach(row => {
        const type = row.querySelector('.event-type').value;
        const teamSelector = row.querySelector('.team-selector') || row.querySelector('.event-team');
        const team = teamSelector ? teamSelector.value : null;

        if (type === 'GOAL') {
            if (team === 'LOCAL') homeScore++;
            else if (team === 'VISITOR') awayScore++;
        }
    });

    const displayHome = document.getElementById('displayHomeScore');
    const displayAway = document.getElementById('displayAwayScore');
    
    if (displayHome) displayHome.value = homeScore;
    if (displayAway) displayAway.value = awayScore;
}

/**
 * 4. ENVÍO DE DATOS AL SERVIDOR
 */
document.getElementById('createMatchForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const matchPayload = {
        id: document.getElementById('matchId')?.value || null,
        localTeam: { id: parseInt(document.getElementById('homeTeam').value) },
        visitorTeam: { id: parseInt(document.getElementById('awayTeam').value) },
        localGoals: parseInt(document.getElementById('displayHomeScore')?.value) || 0,
        visitorGoals: parseInt(document.getElementById('displayAwayScore')?.value) || 0,
        matchDate: document.getElementById('matchDate').value,
        matchTime: document.getElementById('matchTime').value,
        weather: document.getElementById('weather').value,
        
        events: Array.from(document.querySelectorAll('.event-row')).map(row => {
            const type = row.querySelector('.event-type').value;
            const isSub = (type === 'SUBSTITUTION');
            
            return {
                // Captura el minuto desde .event-min (clase de tu template)
                minute: parseInt(row.querySelector('.event-min').value) || 0,
                type: type,
                // Si es gol/tarjeta usa namePlayer, si es cambio usa In/Out
                namePlayer: !isSub ? row.querySelector('.event-player').value : null,
                namePlayerIn: isSub ? row.querySelector('.event-in').value : null,
                namePlayerOut: isSub ? row.querySelector('.event-out').value : null,
                // Captura el equipo desde el selector
                teamRole: (row.querySelector('.team-selector') || row.querySelector('.event-team')).value
            };
        })
    };

    try {
        const response = await fetch('/api/matches', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(matchPayload)
        });

        if (response.ok) {
            Swal.fire({
                title: '¡Guardado!',
                text: 'El partido se ha registrado con éxito.',
                icon: 'success',
                background: '#1e293b',
                color: '#fff'
            }).then(() => window.location.href = '/match-list');
        } else {
            const errorData = await response.json();
            Swal.fire('Error', errorData.error || 'Error al guardar', 'error');
        }
    } catch (err) {
        Swal.fire('Error', 'No se pudo conectar con el servidor', 'error');
    }
});

/**
 * 5. CONTROL DE VISIBILIDAD DE CAMPOS Y CARGA
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

window.onload = async () => {
    await fetchTeams();
    calculateScore();

    const container = document.getElementById('eventsContainer');
    if(container && container.querySelectorAll('.event-row').length === 0) {
        addEventField();
    }
};