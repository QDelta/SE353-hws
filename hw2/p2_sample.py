GVAR = 1

def replace(x):
    global GVAR
    y, GVAR = GVAR, x
    return y
