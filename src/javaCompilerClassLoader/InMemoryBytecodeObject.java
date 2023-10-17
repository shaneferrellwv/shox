package edu.ufl.cise.plcsp23.javaCompilerClassLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class InMemoryBytecodeObject extends SimpleJavaFileObject {
	
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	public InMemoryBytecodeObject(String name, Kind kind) {
        super(URI.create("string:///" + name.replace('.','/') + kind.extension), kind);
	}
	
	public byte[] getBytes() {return bos.toByteArray();}
	
	@Override
	public OutputStream openOutputStream() throws IOException{
		return bos;
	}

}
