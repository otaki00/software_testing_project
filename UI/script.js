window.onload = async function () {
    await fetch('http://localhost:8080/api/v1/update').then(response => response.text()).then(data => {
        console.log(data);
    }).catch(error => {
        console.error('Error Updating Exchange rates:', error);
    });


    await fetch('http://localhost:8080/api/v1/codes').then(response => response.json()).then(data => {
        var select1 = document.getElementById('select1');
        var select2 = document.getElementById('select2');
        data.sort();
        data.forEach(item => {
            let banned_currencies=["AUD","BWP","XCD"]
            if (banned_currencies.includes(item))
            {
                //pass
            }
            else
            {
            var option1 = document.createElement('option');
            option1.value = item;
            option1.text = item;
            select1.appendChild(option1);

            var option2 = document.createElement('option');
            option2.value = item;
            option2.text = item;
            select2.appendChild(option2);
            }
        });
    }).catch(error => console.error('Error:', error));
}

function convert(id) {
    let value = document.getElementById(id).value;
    let fromselect;
    let toselect;

    if (id == 'input1') {
        fromselect = document.getElementById('select1').value;
        toselect = document.getElementById('select2').value;
    } else {
        toselect = document.getElementById('select1').value;
        fromselect = document.getElementById('select2').value;
    }

    if (value.length === 0 || (/^[0-9.]+$/.test(value))) {
        fetch(`http://localhost:8080/api/v1/convert?from=${fromselect}&to=${toselect}&amount=1`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => response.json()).then(data => {
            document.getElementById('r_ex').innerText = `1 ${fromselect} = ${parseFloat(data.toFixed(6))} ${toselect}`;
        }).catch((error) => {
            console.error('Error:', error);
        });

        if (value.length === 0) {
            document.getElementById('r_from').innerText = '';
            document.getElementById('r_to').innerText = '';
            if (id == 'input1') {
                document.getElementById('input2').value = '';
            } else {
                document.getElementById('input1').value = '';
            }
        } else {

            fetch(`http://localhost:8080/api/v1/convert?from=${fromselect}&to=${toselect}&amount=${value}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => response.json()).then(data => {
                if (id == 'input1') {
                    document.getElementById('input2').value = parseFloat(data.toFixed(6));
                } else {
                    document.getElementById('input1').value = parseFloat(data.toFixed(6));
                }
                document.getElementById('r_from').innerText = `${value} ${fromselect} =`;
                document.getElementById('r_to').innerText = `${parseFloat(data.toFixed(6))} ${toselect}`;
            }).catch((error) => {
                console.error('Error:', error);
            });
        }

    } else {
        var wr;
        if (id == 'input1') {
            wr = document.getElementById('C_value1');
        } else {
            wr = document.getElementById('C_value2');
        }
        document.getElementById(id).value = value.replace(/[^0-9.]/g, "");
        wr.style.backgroundColor = '#ff000015'
        wr.style.boxShadow = '0px -4px 0px 0px #ff000025';
        setTimeout(function () {
            wr.style.boxShadow = '0px -4px 0px 0px #2a2b2e';
            wr.style.backgroundColor = '#2d2e30';
        }, 500);
    }

}