// match_logic.js

/**
 * 1. CARGA DE DATOS INICIALES
 */
async function fetchTeams() {
    try {
        const response = await fetch('/api/teams');
        const teams = await response.json();
        const homeSelect = document.getElementById('homeTeam');
        const awaySelect = document.getElementById('awayTeam');

        // Limpiar excepto el placeholder
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
 * 2. GESTIÓN DINÁMICA DE EVENTOS (Goles, Tarjetas...)
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
    calculateScore(); // Recalcular marcador al borrar un gol
}

/**
 * 3. LÓGICA DE MARCADOR EN TIEMPO REAL
 * Suma los goles dependiendo de si el evento es 'GOAL' y qué equipo se seleccionó
 */
function calculateScore() {
    let homeScore = 0;
    let awayScore = 0;

    document.querySelectorAll('.event-row').forEach(row => {
        const type = row.querySelector('.event-type').value;
        const team = row.querySelector('.event-team').value; // 'LOCAL' o 'VISITOR'

        if (type === 'GOAL') {
            if (team === 'LOCAL') homeScore++;
            else awayScore++;
        }
    });

    // Actualizar los inputs visibles del marcador
    document.getElementById('displayHomeScore').value = homeScore;
    document.getElementById('displayAwayScore').value = awayScore;
}

/**
 * 4. ENVÍO DE DATOS AL SERVIDOR (POST/PUT)
 */
document.getElementById('createMatchForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    // Construcción del objeto Match coincidente con Match.java y MatchEvent.java
    const matchPayload = {
        id: document.getElementById('matchId')?.value || null,
        localTeam: { id: parseInt(document.getElementById('homeTeam').value) },
        visitorTeam: { id: parseInt(document.getElementById('awayTeam').value) },
        localGoals: parseInt(document.getElementById('displayHomeScore').value) || 0,
        visitorGoals: parseInt(document.getElementById('displayAwayScore').value) || 0,
        matchDate: document.getElementById('matchDate').value,
        matchTime: document.getElementById('matchTime').value,
        weather: document.getElementById('weather').value,
        // Mapeo de la lista de eventos
        events: Array.from(document.querySelectorAll('.event-row')).map(row => ({
            minute: parseInt(row.querySelector('.event-minute').value),
            type: row.querySelector('.event-type').value,
            namePlayer: row.querySelector('.event-player').value,
            // Enviamos el rol para que el Java asigne el Team correspondiente
            teamRole: row.querySelector('.event-team').value 
        }))
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
                text: 'El partido y sus eventos se han registrado correctamente.',
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
 * 5. INICIALIZACIÓN AL CARGAR LA PÁGINA
 */
window.onload = async () => {
    await fetchTeams();
    
    // Si estamos editando (ya hay ID), calculamos el marcador de lo que venga de DB
    calculateScore();

    // Si es un partido nuevo (container vacío), añadimos la primera fila de evento por defecto
    const container = document.getElementById('eventsContainer');
    if(container && container.querySelectorAll('.event-row').length === 0) {
        addEventField();
    }
};