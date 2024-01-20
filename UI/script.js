window.onload = function () {
    fetch('/end')
        .then(response => response.json())
        .then(data => {
            var select1 = document.getElementById('select1');
            var select2 = document.getElementById('select2');

            data.forEach(item => {
                var option1 = document.createElement('option');
                option1.value = item.value;
                option1.text = item.text;
                select1.appendChild(option1);

                var option2 = document.createElement('option');
                option2.value = item.value;
                option2.text = item.text;
                select2.appendChild(option2);
            });
        })
        .catch(error => console.error('Error:', error));
}

function convert(id) {
    let fromselect;
    let toselect;
    let value = document.getElementById(id).value;

    if (id == 'input1') {
        fromselect = document.getElementById('select1').value;
        toselect = document.getElementById('select2').value;
    } else {
        toselect = document.getElementById('select1').value;
        fromselect = document.getElementById('select2').value;
    }

    let data = { value: value, fromValue: fromselect, toValue: toselect };

    fetch('/end', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            let result = data.doubleValue1;
            let rate = data.doubleValue2;
            console.log("Conversion result: " + result);
            console.log("Conversion rate: " + rate);
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}