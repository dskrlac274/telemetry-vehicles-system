<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Nadzor kazni</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f9;
            color: #333;
        }

        header {
            background-color: #4CAF50;
            color: white;
            padding: 20px;
            text-align: center;
        }

        main {
            padding: 20px;
        }

        #porukaDiv {
            display: none;
            background-color: #ffeb3b;
            padding: 15px;
            border: 1px solid #ffc107;
            border-radius: 5px;
            margin-bottom: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        #poruka {
            margin: 0;
            font-weight: bold;
            font-size: 1.2em;
        }

        #svePorukeDiv {
            background-color: #ffffff;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            max-height: 500px;
            overflow-y: auto;
        }

        .message {
            padding: 10px;
            border-bottom: 1px solid #eee;
        }

        .message:last-child {
            border-bottom: none;
        }

        .heading {
            margin-top: 0;
            color: #4CAF50;
        }

        footer {
            background-color: #333;
            color: white;
            text-align: center;
            padding: 10px;
            position: fixed;
            width: 100%;
            bottom: 0;
        }
    </style>
    <script type="text/javascript">
        var ws;
        var porukaDiv;
        var poruka;
        var svePorukeDiv;
        var timeoutId;

        function init() {
            porukaDiv = document.getElementById("porukaDiv");
            poruka = document.getElementById("poruka");
            svePorukeDiv = document.getElementById("svePorukeDiv");

            ws = new WebSocket("ws://20.24.5.1:8080/dskrlac20_vjezba_08_dz_3_nadzor/kazne");
            ws.onmessage = function(event) {
                obradiPoruku(event.data);
            };
        }

        function obradiPoruku(message) {
            poruka.textContent = message;

            porukaDiv.style.display = "block";

            if (timeoutId) {
                clearTimeout(timeoutId);
            }

            timeoutId = setTimeout(function() {
                porukaDiv.style.display = "none";
            }, 5000);

            var newMessage = document.createElement("div");
            newMessage.className = "message";
            newMessage.textContent = message;
            svePorukeDiv.insertBefore(newMessage, svePorukeDiv.firstChild);
        }

        window.onload = init;
    </script>
</head>
<body>
    <header>
        <h1>Nadzor kazni</h1>
    </header>
    <main>
        <h2 class="heading">Trenutna kazna</h2>
        <div id="porukaDiv">
            <p id="poruka"></p>
        </div>
        <h2 class="heading">Sve kazne</h2>
        <div id="svePorukeDiv"></div>
    </main>
    <footer>
        <p>&copy; 2024 Nadzor kazni. Daniel Škrlac.</p>
    </footer>
</body>
</html>
