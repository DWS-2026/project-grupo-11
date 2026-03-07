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
    // Esta parte ahora es inteligente: detecta si el marcador es un INPUT o un SPAN
    const elements = {
        home: document.getElementById('displayHomeScore'),
        away: document.getElementById('displayAwayScore')
    };

    ['home', 'away'].forEach(side => {
        const el = elements[side];
        const val = side === 'home' ? homeScore : awayScore;
        if (el) {
            // Si es un input usamos .value, si es un span usamos .innerText
            if (el.tagName === 'INPUT') el.value = val;
            else el.innerText = val;
        }
    });
}

/**
 * 3. ENVÍO DE DATOS AL SERVIDOR (Corregido)
 */
// Buscamos cualquiera de los dos formularios
const form = document.getElementById('createMatchForm') || document.getElementById('editMatchForm');

if (form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        calculateScore();

        const formData = new URLSearchParams();

        // Capturar ID si existe (solo en edición)
        const matchId = document.getElementById('matchId')?.value;
        if (matchId && matchId !== "") {
            formData.append('id', matchId); // Importante: el nombre debe ser 'id' para que Spring lo mapee al objeto Match
        }

        formData.append('localTeam.id', document.getElementById('homeTeam').value);
        formData.append('visitorTeam.id', document.getElementById('awayTeam').value);

        // Obtener marcador (sea de span o input)
        const hScore = document.getElementById('displayHomeScore').value || document.getElementById('displayHomeScore').innerText;
        const aScore = document.getElementById('displayAwayScore').value || document.getElementById('displayAwayScore').innerText;

        formData.append('localGoals', hScore);
        formData.append('visitorGoals', aScore);
        formData.append('matchDate', document.getElementById('matchDate').value);
        formData.append('matchTime', document.getElementById('matchTime').value);
        formData.append('weather', document.getElementById('weather').value);

        // Eventos dinámicos
        document.querySelectorAll('.event-row').forEach((row, index) => {
            const eventId = row.querySelector('.event-id')?.value;
            const type = row.querySelector('.event-type').value;
            const role = row.querySelector('.team-selector').value;
            const teamId = (role === 'LOCAL')
                ? document.getElementById('homeTeam').value
                : document.getElementById('awayTeam').value;

            formData.append(`events[${index}].minute`, row.querySelector('.event-min').value || 0);
            formData.append(`events[${index}].type`, type);
            formData.append(`events[${index}].team.id`, teamId);

            if (eventId) {
                formData.append(`events[${index}].id`, eventId);
            }

            if (type !== 'SUBSTITUTION') {
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

            if (response.ok) {
                Swal.fire({ title: '¡Guardado!', icon: 'success', timer: 1500, showConfirmButton: false })
                    .then(() => window.location.href = response.url);
            } else {
                throw new Error("Error en el servidor");
            }
        } catch (err) {
            Swal.fire({ title: 'Error', text: 'No se pudo guardar.', icon: 'error' });
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
    // Primero sincronizamos lo que viene de la DB
    syncSavedEvents();

    // Si no hay eventos, añadimos uno vacío (solo para creación, en edición no hará nada)
    const container = document.getElementById('eventsContainer');
    if (container && container.querySelectorAll('.event-row').length === 0) {
        addEventField();
    }
};

/**
 * 6. SINCRONIZACIÓN DE DATOS DESDE LA DB
 */
function syncSavedEvents() {
    const localTeamId = document.getElementById('homeTeam').value;

    document.querySelectorAll('.event-row').forEach(row => {
        const typeSelect = row.querySelector('.event-type');
        const teamSelect = row.querySelector('.team-selector');

        // 1. Sincronizar Tipo de Evento
        const savedType = typeSelect.getAttribute('data-saved-type');
        if (savedType) {
            typeSelect.value = savedType;
            // Ejecutar la lógica de mostrar/ocultar jugadores (Sustitución)
            toggleEditSubFields(typeSelect);
        }

        // 2. Sincronizar Equipo (Local/Visitante)
        const savedTeamId = teamSelect.getAttribute('data-saved-team-id');
        if (savedTeamId) {
            // Si el ID del equipo del evento coincide con el ID del local, es LOCAL
            teamSelect.value = (savedTeamId === localTeamId) ? 'LOCAL' : 'VISITOR';
        }
    });

    // Recalcular el marcador visual al terminar
    calculateScore();
}