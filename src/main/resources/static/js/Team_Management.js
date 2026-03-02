document.addEventListener('DOMContentLoaded', fetchTeams);

async function fetchTeams() {
    try {
        const response = await fetch('/api/teams');
        const teams = await response.json();
        const tableBody = document.getElementById('teamTableBody');
        
        tableBody.innerHTML = ''; 

        teams.forEach(team => {
            // Lógica para la imagen: Si hay logoData (blob), lo usamos. 
            // Si no, usamos la ruta por defecto.
            const imageSource = team.logoData 
                ? `data:image/png;base64,${team.logoData}` 
                : `/images/default.png`;

            const row = `
                <tr>
                    <td>
                        <img src="${imageSource}" 
                             alt="Logo"
                             style="width: 45px; height: 45px; object-fit: contain; border-radius: 4px;">
                    </td>
                    <td class="text-white fw-bold">${team.name}</td>
                    <td class="text-white-50">${team.stadiumName}</td>
                    <td class="text-center">
                        <div class="d-flex justify-content-center gap-2">
                            <button class="btn btn-sm btn-warning" onclick="editTeam(${team.id})">
                                Editar
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="confirmDelete(${team.id}, '${team.name}')">
                                Borrar
                            </button>
                        </div>
                    </td>
                </tr>
            `;
            tableBody.innerHTML += row;
        });
    } catch (error) {
        console.error("Error al cargar equipos:", error);
    }
}

function editTeam(id) {
    window.location.href = `/EditTeam/${id}`; 
}
async function confirmDelete(id, teamName) {
    const result = await Swal.fire({
        title: '¿Eliminar equipo?',
        text: `Estás a punto de borrar al ${teamName}.`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#ef4444',
        cancelButtonColor: '#64748b',
        confirmButtonText: 'Sí, eliminar',
        background: '#1e293b',
        color: '#ffffff'
    });

    if (result.isConfirmed) {
        try {
            const response = await fetch(`/api/teams/${id}`, { method: 'DELETE' });
            if (response.ok) {
                Swal.fire({ title: 'Eliminado', icon: 'success', background: '#1e293b', color: '#ffffff' });
                fetchTeams(); // Recarga la tabla sin refrescar la página
            }
        } catch (error) {
            console.error("Error:", error);
        }
    }
    
}