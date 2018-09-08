import socket
import RPi.GPIO as GPIO
import wiringpi
import netifaces as ni
import time
 
GPIO.setmode(GPIO.BCM) ##### Setup fuer Steppermotor
GPIO.setwarnings(False)
coil_A_1_pin = 24 # grau
coil_A_2_pin = 23 # violett
coil_B_1_pin = 17 # blau
coil_B_2_pin = 4 # gruen
taster_pin = 7 # gelb
servoPIN1 = 18 # gelb
pos=0
data2=0.0
delay=1
reftime=3200
reftimecount=0

data1 = 45                          # wert fuer servo
data2 = 7                           # wert fuer linear aktuator

wiringpi.wiringPiSetupGpio() ######## Setup fuer Servo (Hardware PWM)
wiringpi.pinMode(servoPIN1, 2)
wiringpi.pwmSetMode(wiringpi.PWM_MODE_MS)
wiringpi.pwmSetRange(1024)
wiringpi.pwmSetClock(375) ####### Einstellung der Richtigen PWM Puls Laenge

varfalse = False
istref = False

# adjust if different
StepCount = 8
Seq = range(0, StepCount)
Seq[0] = [1,0,0,0]
Seq[1] = [1,1,0,0]
Seq[2] = [0,1,0,0]
Seq[3] = [0,1,1,0]
Seq[4] = [0,0,1,0]
Seq[5] = [0,0,1,1]
Seq[6] = [0,0,0,1]
Seq[7] = [1,0,0,1]
 
GPIO.setup(coil_A_1_pin, GPIO.OUT)
GPIO.setup(coil_A_2_pin, GPIO.OUT)
GPIO.setup(coil_B_1_pin, GPIO.OUT)
GPIO.setup(coil_B_2_pin, GPIO.OUT)
GPIO.setup(taster_pin, GPIO.IN, pull_up_down = GPIO.PUD_UP)
#GPIO.setup(servoPIN1, GPIO.OUT)

def maprange( a, b, s): ######### map funktion
    (a1, a2), (b1, b2) = a, b
    if s>a2:
        return b2
    elif s<a1:
        return b1
    else:
        return  b1 + ((s - a1) * (b2 - b1) / (a2 - a1))

def my_callback(channel): ##### Taster fuer Referenzierung
    if channel == taster_pin:
      if GPIO.input(taster_pin):
        ##print "Der Taster wurde geoeffnet."
        global pos
        pos=0
        global istref
        istref = True
        resetStep()
     # else:
        ##print "Der Taster wurde geschlossen."

def setStep(w1, w2, w3, w4):
    GPIO.output(coil_A_1_pin, w1)
    GPIO.output(coil_A_2_pin, w2)
    GPIO.output(coil_B_1_pin, w3)
    GPIO.output(coil_B_2_pin, w4)
 
def resetStep():
    GPIO.output(coil_A_1_pin, 0)
    GPIO.output(coil_A_2_pin, 0)
    GPIO.output(coil_B_1_pin, 0)
    GPIO.output(coil_B_2_pin, 0)

def forward(delay, steps):
    for i in range(steps):
        global pos
        pos+=1
        for j in range(StepCount):
            setStep(Seq[j][0], Seq[j][1], Seq[j][2], Seq[j][3])
            time.sleep(delay)
    
def backwards(delay, steps):
    for i in range(steps):
        global pos
        pos-=1
        for j in reversed(range(StepCount)):
            setStep(Seq[j][0], Seq[j][1], Seq[j][2], Seq[j][3])
            time.sleep(delay)

def referenzieren():
    global reftimecount
    global reftime
    global istref
    global varfalse
    global taster_pin
    global data2
    while reftimecount < reftime and istref == varfalse and GPIO.input(taster_pin) == 0 and data2 == 0:
        #print('tut referenzieren')
        backwards(int(delay) / 1000.0, int(1))
        reftimecount = reftimecount + 1
        global inrangedata1
        global data1
                
        data, addr = sock.recvfrom(1024) # buffer size is 1024 bytes
        MESSAGE1,MESSAGE2 = data.split(",") #Stringteilung Rotation 0-90; Linear 0-2800

        data1 = float(MESSAGE1)
        data2 = float(MESSAGE2)
                
        inrangedata1 = maprange((0,90),(25,66), data1)
        global servoPIN1
        wiringpi.pwmWrite(servoPIN1,int(inrangedata1))
    else:
        #print "Die Zeit ist abgelaufen."
        pos = 0
        istref = True
        resetStep()

                
GPIO.add_event_detect(taster_pin, GPIO.BOTH, callback=my_callback) 

#p1 = GPIO.PWM(servoPIN1, 50) # 50Hz
#p1.start(4) # initialisierung

ni.ifaddresses('wlan0')
ip = ni.ifaddresses('wlan0')[ni.AF_INET][0]['addr']
#print ip

UDP_IP = str(ip)
UDP_PORT = 5560

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.bind((UDP_IP, UDP_PORT))

try:
    while reftimecount < reftime and istref == varfalse and GPIO.input(taster_pin) == 0:
        #print('tut referenzieren')
        #print GPIO.input(taster_pin)
        backwards(int(delay) / 1000.0, int(1))
        reftimecount = reftimecount + 1
        #print reftimecount
    else:
        #print "Die Zeit ist abgelaufen."
        pos = 0
        istref = True   

    while True:
        data, addr = sock.recvfrom(1024) # buffer size is 1024 bytes
        MESSAGE1,MESSAGE2 = data.split(",") #Stringteilung Rotation 0-90; Linear 0-2800

        data1 = float(MESSAGE1)
        data2 = float(MESSAGE2)

        inrangedata1 = maprange((0,90),(25,66), data1)
        wiringpi.pwmWrite(servoPIN1,int(inrangedata1))
        #print inrangedata1

        if data2 > pos and data2 < 2801:
            forward(int(delay) / 1000.0, int(1))
        elif data2 < pos and data2 > 0:
            backwards(int(delay) / 1000.0, int(1))
        elif data2 == 0 and pos != 0:
            reftimecount = 0
            istref = False
            referenzieren()
        elif data2 == pos:
            resetStep()
        #else:
            #print('pos=', pos,'data2', data2)
         
except KeyboardInterrupt:
        GPIO.cleanup()
