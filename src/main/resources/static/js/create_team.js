document.addEventListener('DOMContentLoaded', () => {
    const teamForm = document.getElementById('teamForm');
    const logoInput = document.getElementById('teamLogo');
    const preview = document.getElementById('logoPreview');

    // --- LOGO PREVIEW ---
    logoInput.addEventListener('change', () => {
        const file = logoInput.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                preview.innerHTML = `<img src="${e.target.result}" style="max-width: 150px; margin-top: 10px; border-radius: 8px; border: 2px solid #3b82f6;">`;
            };
            reader.readAsDataURL(file);
        }
    });

    // --- FORM SUBMISSION ---
    teamForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        // 1. Obtain the CSRF token from the hidden input field in the form
        const csrfToken = document.querySelector('input[name="_csrf"]').value;

        // We create a FormData object to send the form data, including the file
        const formData = new FormData();
        
        // If we are editing an existing team, we need to include its ID in the form data
        const teamId = document.getElementById('teamId')?.value;
        if (teamId) {
            formData.append('id', teamId);
        }

        formData.append('name', document.getElementById('teamName').value);
        formData.append('stadiumName', document.getElementById('stadiumName').value);
        formData.append('_csrf', csrfToken);
        
        if (logoInput.files[0]) {
            formData.append('logoFile', logoInput.files[0]); 
        }

        try {
            // We send the form data to the server using fetch, 
            // including the CSRF token in the headers
            const response = await fetch('/admin/teams/save', {
                method: 'POST',
                headers: {
                    // 2. Include the CSRF token in the headers for security
                    'X-CSRF-TOKEN': csrfToken
                },
                body: formData,
                redirect: 'follow'
            });

            if (response.ok || response.redirected) {
                Swal.fire({
                    title: '¡Equipo Guardado!',
                    text: 'Los datos del equipo han sido procesados correctamente.',
                    icon: 'success',
                    background: '#1e293b',
                    color: '#ffffff',
                    timer: 2000,
                    showConfirmButton: false
                }).then(() => {
                    window.location.href = '/admin/teams/list-teams';
                });
            } else {
                throw new Error('Error en la respuesta del servidor');
            }
        } catch (error) {
            console.error("Error al guardar:", error);
            Swal.fire({
                title: 'Error',
                text: 'No se pudo guardar el equipo. Verifica el token CSRF, los permisos y el tamaño de la imagen.',
                icon: 'error',
                background: '#1e293b',
                color: '#ffffff'
            });
        }
    });
});