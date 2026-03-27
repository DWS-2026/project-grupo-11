document.addEventListener('DOMContentLoaded', () => {
    const teamForm = document.getElementById('teamForm');
    const logoInput = document.getElementById('teamLogo');
    const preview = document.getElementById('logoPreview');

    // --- Previsualización del Logo ---
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

    // --- Envío del Formulario ---
    teamForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        // 1. Obtener el token CSRF del campo oculto en el HTML
        const csrfToken = document.querySelector('input[name="_csrf"]').value;

        // Usamos FormData para empaquetar texto y archivos binarios
        const formData = new FormData();
        
        // Si existe el ID (caso edición), lo añadimos
        const teamId = document.getElementById('teamId')?.value;
        if (teamId) {
            formData.append('id', teamId);
        }

        formData.append('name', document.getElementById('teamName').value);
        formData.append('stadiumName', document.getElementById('stadiumName').value);
        
        if (logoInput.files[0]) {
            formData.append('logoFile', logoInput.files[0]); 
        }

        try {
            // Enviamos la petición al controlador de Spring Boot
            const response = await fetch('/admin/teams/save', {
                method: 'POST',
                headers: {
                    // 2. Añadir el token a las cabeceras para evitar el error 403
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