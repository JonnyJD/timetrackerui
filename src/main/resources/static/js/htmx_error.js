function getJson(xhr) {
    if (xhr.getResponseHeader('Content-Type').toLowerCase().includes('application/json')) {
        return JSON.parse(xhr.responseText);
    } else {
        return null;
    }
}

function handleHTMXError(evt) {
    const xhr = evt.detail.xhr;
    let errorText = 'An error occured';
    if (xhr.responseText) {
        const json = getJson(xhr);
        if (json) {
            if (json.message) {
                errorText = json.message;
            } else if (json.error) {
                errorText = json.error;
            }
            if (json.path) {
                errorText += ': ' + json.path;
            }
        } else {
            errorText = xhr.responseText;
        }
    } else {
        if (evt.type === "htmx:sendError") {
            errorText = "could not send the request"
        } else {
            errorText = errorText + ': (type) ' + evt.type;
        }
        if (xhr.responseURL) {
            errorText += ': ' + xhr.responseURL;
        }
    }
    if (xhr.status) {
        errorText = xhr.status + ': ' + errorText;
    }
    document.getElementById('error-output').style.display = 'block';
    document.getElementById('error-output').innerHTML = errorText;
};

document.body.addEventListener('htmx:beforeRequest', function(evt) {
    document.getElementById('error-output').style.display = 'none';
    document.getElementById('error-output').innerHTML = '';

    evt.target.innnerHTML = '';
});

document.body.addEventListener('htmx:sendError', handleHTMXError);
document.body.addEventListener('htmx:responseError', handleHTMXError);

/* vim: set ts=4 sw=4 et: */
