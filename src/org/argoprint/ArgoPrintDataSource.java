package org.argoprint;

import java.lang.*;

public abstract interface ArgoPrintDataSource{
    abstract public boolean hasMethod(String method);
    abstract public Object caller(String call, Object args[]);
}
