#Saleh Marmash
#Omar Hamed
#Majed Alghoul
import time
import os
import unittest
from selenium import webdriver
from selenium.webdriver.support.ui import Select
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
def is_numeric(string):
    try:
        float_value = float(string)
        return True
    except ValueError:
        return False
def check_if_input_takes_nonNumeric_values(string,input):
    print("Started Testing Checking If Input Takes Non Numeric Values For String: \n"+ string)
    if(is_numeric(string)):
        print("Skipped for Wrong Test, You are typing a numerical values only. The given String should not be only Numeric Values")
    else:
        input.send_keys(string)
        taken_string=input.get_attribute("value")
        try:
            assert is_numeric(taken_string)
            print("Passed, Only Numeric values was token (if exist)")
            input.clear()
        except:
            print("Failed, The input took a non-numerical String, Which was: " + taken_string)
            input.clear()
def can_currency_options_be_same(select1,select2,string):
    print("Started Testing if currency options can be the same")
    try:
        select1.select_by_value(string)
    except:
        print("Skipped, Can't find such currency")
        return
    try:
        select2.select_by_value(string)
        print("Error, Selectors got same currency")
    except:
        print("Passed, Selectors got different values")

#Get the current directory of the script
current_directory = os.path.dirname(os.path.realpath(__file__))
#Taking directory before current directory (Parent Directory)
parent_directory = os.path.dirname(current_directory)
#Construct the file path relative to the current directory
file_path = os.path.join(parent_directory, "UI", "index.html")

driver = webdriver.Chrome()
#Wait maximum 10 seconds in each wait
wait=WebDriverWait(driver, 10)

#Opening the website
driver.get(f"file:{file_path}")
time.sleep(5)

#Take the first select
select1=Select(wait.until(EC.element_to_be_clickable((By.ID, "select1"))))
#Take the second select
select2=Select(wait.until(EC.element_to_be_clickable((By.ID, "select2"))))
#Check if currency options can be the same
print("----------------------------------------------------")
can_currency_options_be_same(select1,select2,"ILS")
#Take the first input

input1 = driver.find_element(By.ID, "input1")
#Take the second input
input2 = driver.find_element(By.ID, "input2")
#Check if the input takes non-numerical string or not
print("----------------------------------------------------")
check_if_input_takes_nonNumeric_values("5.5",input1)
print("----------------------------------------------------")
check_if_input_takes_nonNumeric_values("x1t",input1)
print("----------------------------------------------------")
check_if_input_takes_nonNumeric_values("5..5",input1)
