f = open("re.cpp", "r")
content = f.read()
print("\"" + repr(content)[1:-1].replace("\"", "\\\"") + "\"")
