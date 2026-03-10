function editTeam(id) {
    window.location.href = `/teams/edit/${id}`; 
}
async function confirmDeleteTeam(id, teamName) {
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
        // Redirigimos a la ruta del Controller que creamos antes
        // El navegador se encargará de borrar y recargar la página solo
        window.location.href = `/teams/delete/${id}`;
    }
    function confirmDeleteTeam(id, name) {
        Swal.fire({
            title: '¿Eliminar equipo?',
            text: `Estás a punto de borrar al ${name}. Esta acción no se puede deshacer.`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#ef4444',
            cancelButtonColor: '#64748b',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar',
            background: '#1e293b',
            color: '#ffffff'
        }).then((result) => {
            if (result.isConfirmed) {
                // Ejecutamos el borrado redirigiendo a la ruta del Controller
                window.location.href = '/admin/teams/delete/' + id;
            }
        });
    }
}