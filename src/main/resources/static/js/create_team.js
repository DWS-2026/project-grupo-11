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

        // Usamos FormData para empaquetar texto y archivos binarios
        const formData = new FormData();
        formData.append('name', document.getElementById('teamName').value);
        formData.append('stadiumName', document.getElementById('stadiumName').value);
        
        if (logoInput.files[0]) {
            formData.append('logoFile', logoInput.files[0]); 
        }

        try {
            // IMPORTANTE: Al enviar FormData, NO debes poner 'Content-Type': 'application/json'
            const response = await fetch('/api/teams', {
                method: 'POST',
                body: formData 
            });

            if (response.ok) {
                Swal.fire({
                    title: '¡Equipo Creado!',
                    text: 'El equipo y su logo han sido guardados en la base de datos.',
                    icon: 'success',
                    background: '#1e293b',
                    color: '#ffffff',
                    timer: 2000,
                    showConfirmButton: false
                }).then(() => {
                    window.location.href = 'Team_Management_Screen.html';
                });
            } else {
                throw new Error('Error en la respuesta del servidor');
            }
        } catch (error) {
            console.error("Error al guardar:", error);
            Swal.fire({
                title: 'Error',
                text: 'No se pudo guardar el equipo. Revisa el tamaño de la imagen.',
                icon: 'error',
                background: '#1e293b',
                color: '#ffffff'
            });
        }
    });
});