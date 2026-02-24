// match_logic.js

// 1. Cargar equipos desde la base de datos al abrir la página
async function fetchTeams() {
    try {
        const response = await fetch('/api/teams'); 
        const teams = await response.json();
        
        const homeSelect = document.getElementById('homeTeam');
        const awaySelect = document.getElementById('awayTeam');

        // Limpiar opciones previas (excepto la primera de "Seleccione...")
        homeSelect.length = 1;
        awaySelect.length = 1;

        teams.forEach(team => {
            // team.id y team.name coinciden con tu tabla MySQL
            const option = `<option value="${team.id}">${team.name}</option>`;
            homeSelect.innerHTML += option;
            awaySelect.innerHTML += option;
        });
        console.log("Equipos cargados correctamente");
    } catch (error) {
        console.error("Error cargando equipos:", error);
    }
}

// 2. Gestión de campos de eventos
function addEventField() {
    const container = document.getElementById('eventsContainer');
    const template = document.getElementById('eventTemplate');
    const clone = template.content.cloneNode(true);
    container.appendChild(clone);
}

function removeEvent(btn) {
    btn.closest('.event-row').remove();
    calculateScore();
}

// 3. Cálculo dinámico del marcador (Sigue tu esencia)
function calculateScore() {
    let homeScore = 0;
    let awayScore = 0;
    const events = document.querySelectorAll('.event-row');
    
    events.forEach(row => {
        const type = row.querySelector('.event-type').value;
        const team = row.querySelector('.team-selector').value;
        if (type === "GOAL") {
            if (team === "local") homeScore++;
            else awayScore++;
        }
    });
    
    document.getElementById('displayHomeScore').innerText = homeScore;
    document.getElementById('displayAwayScore').innerText = awayScore;
}

// 4. ENVÍO DE DATOS A JAVA (POST)
document.getElementById('createMatchForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    // Juntamos Fecha + Hora para el LocalDateTime de Java
    const fullDate = `${document.getElementById('matchDate').value}T${document.getElementById('matchTime').value}:00`;

    // Estructura idéntica a Match.java
    const matchPayload = {
        localTeam: { id: document.getElementById('homeTeam').value },
        visitorTeam: { id: document.getElementById('awayTeam').value },
        localGoals: parseInt(document.getElementById('displayHomeScore').innerText),
        visitorGoals: parseInt(document.getElementById('displayAwayScore').innerText),
        matchDate: fullDate,
        weather: document.getElementById('weather').value,
        events: Array.from(document.querySelectorAll('.event-row')).map(row => ({
            eventType: row.querySelector('.event-type').value,
            minute: parseInt(row.querySelector('.minute-input').value),
            playerDescription: row.querySelector('.player-info').value
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
                title: '¡Registrado!',
                text: 'El partido y sus eventos se han guardado en MySQL.',
                icon: 'success',
                background: '#1e293b',
                color: '#fff'
            }).then(() => window.location.href = '/matches');
        }
    } catch (err) {
        Swal.fire('Error', 'No se pudo conectar con el servidor', 'error');
    }
});

// Inicialización
window.onload = () => {
    fetchTeams();
    addEventField();
};