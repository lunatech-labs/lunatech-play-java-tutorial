
function deleteProduct(url) {
    var xhr = new XMLHttpRequest();
    xhr.open('DELETE', url, true);
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    xhr.onload = function() {
        if(xhr.readyState == 4 && xhr.status == 200) {
            window.location.replace(xhr.responseURL);
        }
    }

    xhr.send();
}

$(document).ready(function () {

    $('#callbackSelection').change(function ()  {
        var check = $(this).prop('checked');

        if(check) {
            var val = $('#searchImagesWithCallbackAction').val();
            $('#searchForm').prop('action',val);
        } else {
            var val = $('#searchImagesAction').val();
            $('#searchForm').prop('action',val);
        }
    });
});