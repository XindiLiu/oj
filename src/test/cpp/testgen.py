for i in range(0, 10):
    fin = open(f"{i}.in", "w")
    fin.write(str(i))
    fin.close()
    fout = open(f"{i}.out", "w")
    fout.write(str(i))
    fout.close()
