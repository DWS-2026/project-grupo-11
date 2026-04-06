function editTeam(id) {
    window.location.href = `/teams/edit/${id}`; 
}
async function confirmDeleteTeam(id, teamName) {
    const result = await Swal.fire({
        title: '¿Eliminar equipo?',
        text: `Estás a punto de borrar al ${teamName}. Esta acción no se puede deshacer.`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#ef4444',
        cancelButtonColor: '#64748b',
        confirmButtonText: 'Sí, eliminar',
        background: '#1e293b',
        color: '#ffffff'
    });

    if (result.isConfirmed) {
        // Now we can safely redirect to the delete URL, 
        // knowing that the user has confirmed the action.
        window.location.href = `/admin/teams/delete/${id}`;
    }
}