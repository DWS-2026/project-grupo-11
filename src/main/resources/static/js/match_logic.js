// match_logic.js

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
            const option = `<option value="${team.id}">${team.name}</option>`;
            homeSelect.innerHTML += option;
            awaySelect.innerHTML += option;
        }
        
    );
        console.log("Equipos cargados correctamente");
    } catch (error) {
        console.error("Error cargando equipos:", error);
    }
}

// 2. Gestión de campos de eventos
function addEventField() {
    const container = document.getElementById('eventsContainer');
    const template = document.getElementById('eventTemplate');
    if (!template) return; // Seguridad por si no existe el template aún
    const clone = template.content.cloneNode(true);
    container.appendChild(clone);
}

function removeEvent(btn) {
    btn.closest('.event-row').remove();
    calculateScore();
}

// 3. Cálculo dinámico del marcador
// IMPORTANTE: Ahora actualiza .value porque son inputs
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
    
    // CORRECCIÓN: .value para que se vea en los inputs del HTML
    document.getElementById('displayHomeScore').value = homeScore;
    document.getElementById('displayAwayScore').value = awayScore;
}

// 4. ENVÍO DE DATOS A JAVA (POST)
document.getElementById('createMatchForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    // Juntamos Fecha + Hora
    const dateInput = document.getElementById('matchDate').value;
    const timeInput = document.getElementById('matchTime').value;
    const fullDate = `${dateInput}T${timeInput}:00`;

    // Estructura idéntica a Match.java
    const matchPayload = {
        localTeam: { id: parseInt(document.getElementById('homeTeam').value) },
        visitorTeam: { id: parseInt(document.getElementById('awayTeam').value) },
        // CORRECCIÓN: Usamos .value
        localGoals: parseInt(document.getElementById('displayHomeScore').value) || 0,
        visitorGoals: parseInt(document.getElementById('displayAwayScore').value) || 0,
        matchDate: dateInput, // Enviamos solo fecha para LocalDate
        matchTime: timeInput, // Enviamos solo hora para LocalTime
        weather: document.getElementById('weather').value,
        events: Array.from(document.querySelectorAll('.event-row')).map(row => ({
            eventType: row.querySelector('.event-type').value,
            minute: parseInt(row.querySelector('.minute-input').value),
            playerDescription: row.querySelector('.player-info').value
        }))
    };

    console.log("Enviando datos:", matchPayload);

    try {
        const response = await fetch('/api/matches', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(matchPayload)
        });

        if (response.ok) {
            Swal.fire({
                title: '¡Registrado!',
                text: 'El partido se ha guardado y el estadio se asignó automáticamente.',
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

// Inicialización
window.onload = async () => {
    // 1. Cargamos los equipos primero
    await fetchTeams();
    
    // 2. Calculamos el marcador actual (por si ya hay eventos/goles en la edición)
    calculateScore();

    // 3. Solo añade un campo de evento vacío si es un partido nuevo 
    // (si no hay filas de eventos ya creadas por Mustache)
    const container = document.getElementById('eventsContainer');
    if(container && container.querySelectorAll('.event-row').length === 0) {
        addEventField();
    }
    
    console.log("Datos del partido inicializados: Hora, Clima y Resultado listos.");
};