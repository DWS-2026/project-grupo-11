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

    // 1. Actualizar Inputs ocultos (para el envío del form)
    document.getElementById('displayHomeScore').value = homeScore;
    document.getElementById('displayAwayScore').value = awayScore;

    // 2. ACTUALIZAR INTERFAZ VISUAL (lo que el usuario ve)
    document.getElementById('scoreHome').innerText = homeScore;
    document.getElementById('scoreAway').innerText = awayScore;
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

        // Captura inteligente de marcador
        const hDisp = document.getElementById('displayHomeScore');
        const aDisp = document.getElementById('displayAwayScore');
        const hScore = hDisp.value || hDisp.innerText || 0;
        const aScore = aDisp.value || aDisp.innerText || 0;

        formData.append('localGoals', hScore);
        formData.append('visitorGoals', aScore);
        formData.append('matchDate', document.getElementById('matchDate').value);
        formData.append('matchTime', document.getElementById('matchTime').value);
        formData.append('weather', document.getElementById('weather').value);
        
        // ... dentro del form.addEventListener('submit' ...

        // 1. Captura del Estadio (Lo que preguntaste)
        const stadiumValue = document.getElementById('stadium')?.value || "Estadio Municipal";
        formData.append('stadium', stadiumValue);

        // 2. Captura de Eventos con ID (Asegúrate de que el bucle se vea así)
        document.querySelectorAll('.event-row').forEach((row, index) => {
            const eventId = row.querySelector('.event-id')?.value;
            if (eventId) {
                formData.append(`events[${index}].id`, eventId); // VITAL para editar sin duplicar
            }
        // ... resto de tu lógica de campos ...
        });

        document.querySelectorAll('.event-row').forEach((row, index) => {
            // 1. Capturar el ID del evento si existe (para edición)
            const eventId = row.querySelector('.event-id')?.value;
            if (eventId) {
                formData.append(`events[${index}].id`, eventId);
            }
            const type = row.querySelector('.event-type').value;
            // ... resto de tu lógica de capturar tipo, equipo, minuto ...
            const role = row.querySelector('.team-selector').value;
            const teamId = (role === 'LOCAL')
                ? document.getElementById('homeTeam').value
                : document.getElementById('awayTeam').value;

            formData.append(`events[${index}].minute`, row.querySelector('.event-min').value || 0);
            formData.append(`events[${index}].type`, type);
            formData.append(`events[${index}].team.id`, teamId);

            if (eventId) formData.append(`events[${index}].id`, eventId);

            if (type !== 'SUBSTITUTION') {
                formData.append(`events[${index}].namePlayer`, row.querySelector('.event-player').value);
            } else {
                formData.append(`events[${index}].namePlayerIn`, row.querySelector('.event-in').value);
                formData.append(`events[${index}].namePlayerOut`, row.querySelector('.event-out').value);
            }
        });

        try {
            // RUTA CORREGIDA: /admin/match/save (en singular según tu MatchController)
            const response = await fetch('/admin/match/save', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            if (response.ok) {
                Swal.fire({ title: '¡Guardado!', icon: 'success', timer: 1500, showConfirmButton: false })
                    .then(() => window.location.href = '/admin/ModifyMatch'); 
            } else {
                throw new Error("Error en el servidor");
            }
        } catch (err) {
            Swal.fire({ title: 'Error', text: 'No se pudo guardar el partido.', icon: 'error' });
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
 * 5. CARGA INICIAL Y ESCUCHA DE CAMBIOS
 */
window.onload = () => {
    syncSavedEvents();

    // HACER EL MARCADOR DINÁMICO: Escuchar cambios en el contenedor
    const container = document.getElementById('eventsContainer');
    if (container) {
        container.addEventListener('change', (e) => {
            if (e.target.classList.contains('event-type') || e.target.classList.contains('team-selector')) {
                calculateScore();
            }
        });

        if (container.querySelectorAll('.event-row').length === 0) {
            addEventField();
        }
    }
};

function syncSavedEvents() {
    const homeTeamEl = document.getElementById('homeTeam');
    if(!homeTeamEl) return;
    
    const localTeamId = homeTeamEl.value;

    document.querySelectorAll('.event-row').forEach(row => {
        const typeSelect = row.querySelector('.event-type');
        const teamSelect = row.querySelector('.team-selector');

        const savedType = typeSelect.getAttribute('data-saved-type');
        if (savedType) {
            typeSelect.value = savedType;
            toggleEditSubFields(typeSelect);
        }

        const savedTeamId = teamSelect.getAttribute('data-saved-team-id');
        if (savedTeamId) {
            teamSelect.value = (savedTeamId === localTeamId) ? 'LOCAL' : 'VISITOR';
        }
    });
    calculateScore();
}