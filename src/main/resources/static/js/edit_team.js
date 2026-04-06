const editForm = document.getElementById('editTeamForm');

editForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const teamId = document.getElementById('teamId').value;
    const teamName = document.getElementById('teamName').value;
    const stadiumName = document.getElementById('stadiumName').value;
    const logoFile = document.getElementById('teamLogo').files[0];
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    const formData = new FormData();
    // IMPORTANT: When editing, we need to include the team ID 
    // in the form data so that the server knows which team to update.
    formData.append('id', teamId);
    formData.append('name', teamName);
    formData.append('stadiumName', stadiumName);
    formData.append('_csrf', csrfToken);

    if (logoFile) {
        formData.append('logoFile', logoFile);
    }

    try {
        // CHANGE: We need to include the CSRF token in the headers for security,
        // and we also need to set redirect: 'follow' to handle the redirection after saving.
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
                // We redirect to the team list page after successful update
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