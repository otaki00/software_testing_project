#Saleh Marmash
#Omar Hamed
#Majed Alghoul
import time
import os
import unittest
import pytest
from selenium import webdriver
from selenium.webdriver.support.ui import Select
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
@pytest.fixture(scope="module")
def driver_setup(request):
    driver = webdriver.Chrome()
    wait = WebDriverWait(driver, 10)
    
    #Opening the website
    current_directory = os.path.dirname(os.path.realpath(__file__))
    parent_directory = os.path.dirname(current_directory)
    file_path = os.path.join(parent_directory, "UI", "index.html")
    driver.get(f"file:{file_path}")

    def teardown():
        driver.quit()

    request.addfinalizer(teardown)
    return driver, wait
    time.sleep(5)
def is_numeric(string):
    try:
        float_value = float(string)
        return True
    except ValueError:
        return False
def check_if_input_takes_nonNumeric_values(driver,input,string):
    print("Started Testing Checking If Input Takes Non Numeric Values For String: \n"+ string)
    if(is_numeric(string)):
        print("Skipped for Wrong Test, You are typing a numerical values only. The given String should not be only Numeric Values")
    else:
        input.send_keys(string)
        taken_string=input.get_attribute("value")
        try:
            assert is_numeric(taken_string) in driver.title
            print("Passed, Only Numeric values was token (if exist)")
            input.clear()
        except:
            print("Failed, The input took a non-numerical String, Which was: " + taken_string)
            input.clear()
def can_currency_options_be_same(driver,select1,select2,string):
    print("Started Testing if currency options can be the same")
    try:
        select1.select_by_value(string)
    except:
        print("Skipped, Can't find such currency")
        return
    try:
        select2.select_by_value(string)
    except:
        pass
    try:
        assert select1.value!=select2.value in driver.title
        print("Passed, Selectors got different values")
    except:
        print("Error, Selectors got same currency")
def check_inputX_reflect_inputY(driver,select1,select2,input1,input2,string,currency1,currency2):
    select1.select_by_value(currency1)
    select2.select_by_value(currency2)
    input1.send_keys(string)
    time.sleep(1)
    rate=driver.find_element(By.ID, "r_ex").text.split(" ")
    rate=rate[3]
    print("Rate is: "+rate)
    result = float(rate) * float(input1.get_attribute("value"))
    rounded_result = round(result, 3)
    input_two_result=float(input2.get_attribute("value"))
    input_two_value=round(input_two_result, 3)
    print("Total is: " + str(input_two_value))
    try:
        assert rounded_result==input_two_value in driver.title
        print("Passed, Input two has got right values")
    except:
        print("Failed, the Input two doesn't have right value: "+str(rounded_result))

def test_can_currency_options_be_same(driver_setup):
    driver, wait = driver_setup
    select1 = Select(wait.until(EC.element_to_be_clickable((By.ID, "select1"))))
    select2 = Select(wait.until(EC.element_to_be_clickable((By.ID, "select2"))))
    can_currency_options_be_same(driver, select1, select2, "ILS")
def test_check_if_input_takes_nonNumeric_values(driver_setup):
    driver, wait = driver_setup
    input1 = driver.find_element(By.ID, "input1")
    print("----------------------------------------------------")
    check_if_input_takes_nonNumeric_values(driver, input1, "5.5")
    time.sleep(3)
    print("----------------------------------------------------")
    check_if_input_takes_nonNumeric_values(driver, input1, "x1t")
    time.sleep(3)
    print("----------------------------------------------------")
    check_if_input_takes_nonNumeric_values(driver, input1, "5..5")
def test_inputX_reflect_inputY(driver_setup):
    driver, wait = driver_setup
    select1 = Select(wait.until(EC.element_to_be_clickable((By.ID, "select1"))))
    select2 = Select(wait.until(EC.element_to_be_clickable((By.ID, "select2"))))
    input1 = driver.find_element(By.ID, "input1")
    input2 = driver.find_element(By.ID, "input2")
    print("----------------------------------------------------")
    check_inputX_reflect_inputY(driver, select1, select2, input1, input2, "5.1", "USD", "ILS")
    time.sleep(3)