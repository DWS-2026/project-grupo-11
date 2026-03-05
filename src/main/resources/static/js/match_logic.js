/**
 * 1. GESTIÓN DINÁMICA DE EVENTOS
 */
function addEventField() {
    const container = document.getElementById('eventsContainer');
    const template = document.getElementById('eventTemplate');
    if (!template) return;
    
    const clone = template.content.cloneNode(true);
    container.appendChild(clone);
    // Recalculamos por si el template viene con un gol por defecto
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

    // Escaneamos todas las filas de eventos
    document.querySelectorAll('.event-row').forEach(row => {
        const type = row.querySelector('.event-type').value;
        const teamSelector = row.querySelector('.team-selector');
        const team = teamSelector ? teamSelector.value : null;

        if (type === 'GOAL') {
            if (team === 'LOCAL') homeScore++;
            else if (team === 'VISITOR') awayScore++;
        }
    });

    // A. Actualizamos los inputs ocultos para MySQL
    const displayHomeInput = document.getElementById('displayHomeScore');
    const displayAwayInput = document.getElementById('displayAwayScore');
    if (displayHomeInput) displayHomeInput.value = homeScore;
    if (displayAwayInput) displayAwayInput.value = awayScore;

    // B. ACTUALIZACIÓN VISUAL (Marcador de números grandes)
    const scoreHomeDiv = document.getElementById('scoreHome');
    const scoreAwayDiv = document.getElementById('scoreAway');
    if (scoreHomeDiv) scoreHomeDiv.innerText = homeScore;
    if (scoreAwayDiv) scoreAwayDiv.innerText = awayScore;
}

/**
 * Actualiza los nombres de los equipos en el marcador
 */
function updateScoreLabels() {
    const homeSelect = document.getElementById('homeTeam');
    const awaySelect = document.getElementById('awayTeam');
    const homeBadge = document.getElementById('homeBadge');
    const awayBadge = document.getElementById('awayBadge');

    if (homeSelect.selectedIndex > 0) {
        homeBadge.innerText = homeSelect.options[homeSelect.selectedIndex].text;
    }
    if (awaySelect.selectedIndex > 0) {
        awayBadge.innerText = awaySelect.options[awaySelect.selectedIndex].text;
    }
}

/**
 * 3. ENVÍO DE DATOS AL SERVIDOR
 */
document.getElementById('createMatchForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    // Aseguramos que el marcador esté actualizado antes de enviar
    calculateScore();

    const matchPayload = {
        id: document.getElementById('matchId')?.value || null,
        localTeam: { id: parseInt(document.getElementById('homeTeam').value) },
        visitorTeam: { id: parseInt(document.getElementById('awayTeam').value) },
        localGoals: parseInt(document.getElementById('displayHomeScore').value),
        visitorGoals: parseInt(document.getElementById('displayAwayScore').value),
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
                teamRole: row.querySelector('.team-selector').value
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
                title: '¡Partido Guardado!',
                text: `Marcador final: ${matchPayload.localGoals} - ${matchPayload.visitorGoals}`,
                icon: 'success',
                background: '#1e293b',
                color: '#fff'
            }).then(() => {
                window.location.href = '/matches';
            });
        } else {
            throw new Error("Error en el servidor");
        }
    } catch (err) {
        Swal.fire({
            title: 'Error',
            text: 'No se pudo guardar en MySQL. Revisa la conexión.',
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