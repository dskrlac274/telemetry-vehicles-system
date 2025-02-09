document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById('forma-pretrazivanje');
    if (form) {
        form.addEventListener('submit', function(event) {
            event.preventDefault();
            const odVremenaInput = document.getElementById('odVremena').value;
            const doVremenaInput = document.getElementById('doVremena').value;
            const odVremena = new Date(odVremenaInput).getTime();
            const doVremena = new Date(doVremenaInput).getTime();
            form.querySelector('input[name="odVremena"]').value = odVremena;
            form.querySelector('input[name="doVremena"]').value = doVremena;
            form.submit();
        });
    }

    const vanjskiTable = $('#vanjskiTable');
    vanjskiTable.DataTable({
        searching: false,
        paging: true,
        info: true,
        ordering: true
    });
});
