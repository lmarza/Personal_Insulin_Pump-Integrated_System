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

function connect()
{
    let socket = new SockJS('/gs-guide-websocket');
    let stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame)
    {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/measurements', function (message)
        {
            let parsedMeasurement = JSON.parse(message.body);
            //optional, it wants a Measurement type
            let measurement = Object.assign(new Measurement, parsedMeasurement);

            document.getElementById("r2").innerText = measurement.r2.toFixed(2) + " μg/ml";
            document.getElementById("compDose").innerText = measurement.compDose + " unit(s)";

            if(document.getElementById("state").innerText.includes("Booting"))
            {
                document.getElementById("state").innerText = "RUNNING";
            }
        });

        stompClient.subscribe('/topic/state', function (message)
        {
            let stateElement = document.getElementById("state");

            if(stateElement.innerText.includes("RUNNING")  || stateElement.innerText.includes("Booting"))
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