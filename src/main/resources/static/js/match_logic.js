/**
 * 1. GESTIÓN DINÁMICA DE EVENTOS
 * (La carga de equipos ahora la hace Mustache directamente en el HTML)
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
 * 2. LÓGICA DE MARCADOR EN TIEMPO REAL
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
 * 3. ENVÍO DE DATOS AL SERVIDOR
 * Nota: El envío sigue siendo JSON hacia un RestController de partidos.
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
                minute: parseInt(row.querySelector('.event-min').value) || 0,
                type: type,
                namePlayer: !isSub ? row.querySelector('.event-player').value : null,
                namePlayerIn: isSub ? row.querySelector('.event-in').value : null,
                namePlayerOut: isSub ? row.querySelector('.event-out').value : null,
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
            }).then(() => {
                // Redirige a la lista de partidos (ruta del Controller)
                window.location.href = '/matches';
            });
        } else {
            const errorData = await response.json();
            Swal.fire({
                title: 'Error',
                text: errorData.error || 'Error al guardar',
                icon: 'error',
                background: '#1e293b',
                color: '#fff'
            });
        }
    } catch (err) {
        Swal.fire({
            title: 'Error',
            text: 'No se pudo conectar con el servidor',
            icon: 'error',
            background: '#1e293b',
            color: '#fff'
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
 * 5. CARGA INICIAL DE LA PÁGINA
 */
window.onload = () => {
    // Ya no llamamos a fetchTeams(), Mustache ya cargó los equipos
    calculateScore();

    const container = document.getElementById('eventsContainer');
    // Si el contenedor está vacío (nueva creación), añadimos la primera fila de evento
    if(container && container.querySelectorAll('.event-row').length === 0) {
        addEventField();
    }
};