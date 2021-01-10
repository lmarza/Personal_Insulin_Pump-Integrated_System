/*
class Measurement
{
    constructor(r0, r1, r2, compDose)
    {
        this.r0 = r0;
        this.r1 = r1;
        this.r2 = r2;
        this.compDose = compDose;
    }
}

 */

function connect()
{
    var socket = new SockJS('/gs-guide-websocket');
    var stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame)
    {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/measurements', function (message)
        {
            var parsedMeasurement = JSON.parse(message.body);
            //optional, it wants a Measurement type
            //let measurement = Object.assign(new Measurement, parsedMeasurement);

            document.getElementById("r2").innerText = parsedMeasurement.r2.toFixed(2) + " μg/ml";
            document.getElementById("compDose").innerText = parsedMeasurement.compDose + " unit(s)";

            if(document.getElementById("state").innerText.indexOf("Booting") !== -1) //.includes("Booting"))
            {
                document.getElementById("state").innerText = "RUNNING";
            }

            console.log(parsedMeasurement);
        });

        stompClient.subscribe('/topic/state', function (message)
        {
            var stateElement = document.getElementById("state");

            //.includes("RUNNING")
            if(stateElement.innerText.indexOf("RUNNING") !== -1  || stateElement.innerText.indexOf("Booting") !== -1)//.includes("Booting"))
                stateElement.innerText = "";

            stateElement.innerText = message.body + "\n" + stateElement.innerText;

            stateElement.parentElement.style.borderColor = "darkred";
            stateElement.parentElement.style.backgroundColor = "darkred";
            stateElement.parentElement.style.color = "white";

            document.getElementById("rebootForm").style.display = "block";
        });
    });
}

connect();