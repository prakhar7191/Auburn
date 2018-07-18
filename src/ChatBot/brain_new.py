import socket

############## IP ###############
UDP_IP = '127.0.0.1'#"192.168.1.217"
UDP_PORT = 5123
################################



MESSAGE = ""

sock = socket.socket(socket.AF_INET,socket.SOCK_DGRAM) # UDP4


############ Chat Bot Code ############

from chatterbot.trainers import ListTrainer
from chatterbot.trainers import ChatterBotCorpusTrainer
from chatterbot import ChatBot
import datetime
import os
import csv
import random

def insert(original, pos):
    return (original[:pos] +" "+ original[pos]+" "+original[pos+1:])

bot=ChatBot('Test')

chatbot = ChatBot("Training Example")
chatbot.set_trainer(ChatterBotCorpusTrainer)

chatbot.train(
    "chatterbot.corpus.english.conversations",
    "chatterbot.corpus.english.greetings"
    
)

bot.set_trainer(ListTrainer)

mathbot = ChatBot(
        "Math Bot",
        logic_adapters=[
        "chatterbot.logic.MathematicalEvaluation",
        ],
        input_adapter="chatterbot.input.VariableInputTypeAdapter",
        output_adapter="chatterbot.output.OutputAdapter"
        )

timebot = ChatBot(
        "Time Bot",
        logic_adapters=[
        "chatterbot.logic.TimeLogicAdapter",
        ],
        input_adapter="chatterbot.input.VariableInputTypeAdapter",
        output_adapter="chatterbot.output.OutputAdapter"
        )


#dataset = pd.read_csv('shortjokes.csv')
#y=dataset.iloc[:,2:].values
with open('C:\\Users\\Shreyas\\Documents\\NetBeansProjects\\PersonalAssistant\\src\\ChatBot\\shortjokes.csv') as csvfile:
    readCSV=csv.reader(csvfile,delimiter=',')
    jokes=[]
    for row in readCSV:
        joke= row[1:]
        jokes.append(joke)

for _file in os.listdir('C:\\Users\\Shreyas\\Documents\\NetBeansProjects\\PersonalAssistant\\src\\ChatBot\\LeDs'):
    chats=open('C:\\Users\\Shreyas\\Documents\\NetBeansProjects\\PersonalAssistant\\src\\ChatBot\\LeDs\\'+_file , 'r').readlines()
    bot.train(chats)

j=0
x=0
#flag=0
while True:
    request = raw_input()
    if (request=="shutdown") :
        break
    #if flag==1:
    #    flag=0
     #   print("okay !")
     #   continue
    chars = set('*^/+-')
    abc ="^/*+-"
    for i in range(0, len(abc)):
        if abc[i] in request:
            request = insert(request,request.find(abc[i]))
            
    if any((c in chars) for c in request):
        response = mathbot.get_response(request)
    elif 'time' in request:
        response = timebot.get_response(request)
    elif ('date' in request) or ('what' in request and 'today' in request):
        print('Today is '+str(datetime.date.today()))
        continue    
    elif 'joke' in request:
        j = random.randint(0,501)
        '''
        while j<100:
            j=j+1
            if j==100:
                j=0
            print(jokes[j])                
            break
        '''
        MESSAGE = str(jokes[j])
        sock.sendto(MESSAGE,(UDP_IP,UDP_PORT))
        print(MESSAGE)
        print(jokes[j])
        continue
    else:
        z1=chatbot.get_response(request)
        z2=bot.get_response(request)
        z3=mathbot.get_response(request)
        response = z1
        if z2.confidence > response.confidence:
            response = z2    
        if z3.confidence > response.confidence:
            response = z3
            if z2.confidence > z3.confidence:
                response = z2
                
    #if (str(response).strip())[len(str(response))-1] == "?":
     #   flag=1
    
    if response.confidence>0.4:
        if response=='\n':
            MESSAGE = str("failed to load results")
            sock.sendto(MESSAGE,(UDP_IP,UDP_PORT))
            print(MESSAGE)
            print("failed to load results")
        else :
            if 'George' in (str(response)):
                print('I am Auburn , the assistant')
                continue
            MESSAGE = str(response)
            sock.sendto(MESSAGE,(UDP_IP,UDP_PORT))
            print(MESSAGE)
            print('Bot: ',response)
    else:
        MESSAGE = str("failed to load results")
        sock.sendto(MESSAGE,(UDP_IP,UDP_PORT))
        print(MESSAGE)
        print("Failed to load results")
        


############ Chat Bot Code ############




