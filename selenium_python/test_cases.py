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
    time.sleep(5)
    return driver, wait
def is_negative(number):
    return number >= 0
def is_numeric(string):
    try:
        float_value = float(string)
        return True
    except ValueError:
        return False
def check_if_input_takes_nonNumeric_values(driver,input,string):
    print("Started Testing Checking If Input Takes Non Numeric Values For String: \n"+ string)
    
    input.send_keys(string)
    taken_string=input.get_attribute("value")
    assert is_numeric(taken_string) ,"Failed, The input took a non-numerical String, Which was: " + taken_string
    print("Passed, Only Numeric values was token (if exist)")
       

def can_currency_options_be_same(select1,select2,string):
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
    is_equal=select1.first_selected_option.get_attribute("value")!=(select2.first_selected_option.get_attribute("value"))
    assert is_equal,"Failed, Currency one is the same type of Currency two."
    print("Passed, Selectors got different values")
    
def check_inputX_reflect_inputY(driver,select1,select2,input1,input2,string,currency1,currency2):
    select1.select_by_value(currency1)
    select2.select_by_value(currency2)
    input1.send_keys(string)
    time.sleep(1)
    rate=driver.find_element(By.ID, "r_ex").text.split(" ")
    rate=rate[3]
    print("Rate is: "+rate)
    result = float(rate) * float(input1.get_attribute("value"))
    rounded_result = round(result, 2)
    input_two_result=float(input2.get_attribute("value"))
    input_two_value=round(input_two_result, 2)
    print("Total is: " + str(input_two_value))
    assert rounded_result==input_two_value,"Failed, the Input two doesn't have right value: "+str(rounded_result)
    print("Passed, Input two has got right values")
def check_if_input_takes_only_positive_value(input):
    string="-5"
    input.send_keys(string)
    assert(is_negative(float(input.get_attribute("value")))), "Failed, Took a negative value"
    print ("Success, input only took positive value")


def test_can_currency_options_be_same(driver_setup):
    driver, wait = driver_setup
    select1 = Select(wait.until(EC.element_to_be_clickable((By.ID, "select1"))))
    select2 = Select(wait.until(EC.element_to_be_clickable((By.ID, "select2"))))
    can_currency_options_be_same(select1, select2, "ILS")
def test_check_if_input_takes_nonNumeric_values(driver_setup):
    driver, wait = driver_setup
    input1 = driver.find_element(By.ID, "input1")
    print("----------------------------------------------------")
    input1.clear()
    check_if_input_takes_nonNumeric_values(driver, input1, "5.5")
    
    time.sleep(3)
    print("----------------------------------------------------")
    input1.clear()
    check_if_input_takes_nonNumeric_values(driver, input1, "x1t")
    
    time.sleep(3)
    print("----------------------------------------------------")
    input1.clear()
    check_if_input_takes_nonNumeric_values(driver, input1, "5..5")
    
def test_inputX_reflect_inputY(driver_setup):
    driver, wait = driver_setup
    select1 = Select(wait.until(EC.element_to_be_clickable((By.ID, "select1"))))
    select2 = Select(wait.until(EC.element_to_be_clickable((By.ID, "select2"))))
    input1 = driver.find_element(By.ID, "input1")
    input2 = driver.find_element(By.ID, "input2")
    print("----------------------------------------------------")
    input1.clear()
    input2.clear()
    check_inputX_reflect_inputY(driver, select1, select2, input1, input2, "5.1", "USD", "ILS")
    print("----------------------------------------------------")
    input1.clear()
    input2.clear()
    check_inputX_reflect_inputY(driver, select1, select2, input1, input2, ".1", "USD", "ILS")
    time.sleep(3)
def test_if_input_takes_only_positive_value(driver_setup):
    driver, wait = driver_setup
    input1 = driver.find_element(By.ID, "input1")
    check_if_input_takes_only_positive_value(input1)