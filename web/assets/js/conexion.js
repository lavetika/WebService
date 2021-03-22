var ws = null;
var btnConectar = null;
var txtUsuario = null;
var cbxDestinatario = null;
//funcion que maneja el click del botón enviar
function click() {
    var txtChat = document.getElementById("txtChat");
    var txtMsj = document.getElementById("txtMsj");
    txtChat.value += "YO: " + txtMsj.value + "\n";
    //aqui se manda el mensaje de la caja de texto al websocket
    var mensaje=new Object();
    mensaje.tipo="mensaje";
    mensaje.destinatario=cbxDestinatario.value
    mensaje.contenido=txtMsj.value
    mensaje.emisor=txtUsuario.value
    ws.send(JSON.stringify(mensaje));
    txtMsj.value=""
    txtMsj.focus()
}
//al cargarse la página se ejecuta esta función
window.onload = function () {
    txtUsuario = document.getElementById("txtUsuario");
    btnConectar = document.getElementById("btnConectar")
    btnConectar.onclick = conectar
}

function conectar() {
    var divChat = document.getElementById("contenidoChat")
    var txtChat = document.getElementById("txtChat");
    var btnEnviar = document.getElementById("btnEnviar");
    cbxDestinatario = document.getElementById("cbxDestinatario")

    // se asigna el evento del click
    btnEnviar.onclick = click;
    //hace la conexión al web socket
    ws = new WebSocket
        ("ws://localhost:8084/ClienteWebsocket/websocket?" + txtUsuario.value);
    //la función que se ejecuta al abrirse la conexión
    ws.onopen = function () {
        //se envía este mensaje al websocket
        btnConectar.hidden = true
        txtUsuario.disabled = true
        divChat.hidden = false
        txtChat.value="Conectado\n"
    };
    //se asigna y define la función a ejecutarse cada que llegue un mensaje desde el websocket
    ws.onmessage = function (evt) {
        var msg = evt.data;
        var json = JSON.parse(msg);
        if (json.tipo == "conexion") {
            cbxDestinatario.innerHTML = `
                <option value="todos">Todos</option>
            `
            json.users.forEach(user => {
                cbxDestinatario.innerHTML += `
                    <option value="${user.id}">${user.nombre}</option>
                `
            });
        }else if(json.tipo == "mensaje"){
            var dice;
            if(json.cuerpo.destinatario=="todos"){
                dice="dice a todos:"
            }else{
                dice="dice por privado:"
            }
            txtChat.value+=`${json.cuerpo.emisor} ${dice} ${json.cuerpo.contenido}\n`;
        }

        // var txtChat = document.getElementById("txtChat");
        // txtChat.value += received_msg+"\n";
    };

    ws.onclose = function (evt) {
        alert("No se pudo conectar porque: " + evt.reason);
        txtUsuario.disabled = false;
        btnConectar.hidden = false;
        divChat.hidden = true;
    };
}