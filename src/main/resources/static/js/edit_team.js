const editForm = document.getElementById('editTeamForm');

editForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const teamId = document.getElementById('teamId').value;
    const teamName = document.getElementById('teamName').value;
    const stadiumName = document.getElementById('stadiumName').value;
    const logoFile = document.getElementById('teamLogo').files[0];

    const formData = new FormData();
    // IMPORTANTE: Incluimos el ID para que el Controller sepa que es una actualización
    formData.append('id', teamId);
    formData.append('name', teamName);
    formData.append('stadiumName', stadiumName);

    if (logoFile) {
        formData.append('logoFile', logoFile);
    }

    try {
        // CAMBIO: Enviamos a /teams/save mediante POST (el estándar de los Controllers de vista)
        const response = await fetch(`/admin/teams/save`, {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            Swal.fire({
                title: '¡Actualizado!',
                text: `Los cambios en ${teamName} se han guardado con éxito.`,
                icon: 'success',
                background: '#1e293b',
                color: '#ffffff',
                timer: 1500,
                showConfirmButton: false
            }).then(() => {
                // Redirigimos a la ruta del controlador de la lista
                window.location.href = '/admin/teams/list-teams';
            });
        } else {
            throw new Error('Failed to update team');
        }
    } catch (error) {
        console.error("Error updating team:", error);
        Swal.fire({
            title: 'Error',
            text: 'No se pudo actualizar el equipo.',
            icon: 'error',
            background: '#1e293b',
            color: '#ffffff'
        });
    }
});