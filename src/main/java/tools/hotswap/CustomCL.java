package tools.hotswap;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;

import tools.file.FileUtils;

/**
 * reference --
 * http://www.ibm.com/developerworks/cn/java/j-lo-hotswapcls/index.html
 * 
 * @author Whitman.Yang
 */
public class CustomCL extends ClassLoader{

	// the base directory for the classes which need hot swapping
	private String basedir;

	// the names of the classes which need hot swapping
	private HashSet<String> dynclazns;

	public CustomCL(String basedir, String[] clazns) {
		// assign the parent class loader to null to realize customized class loading
		super(null);
		this.basedir = basedir;		
		customizedLoadClazz(clazns);
	}

	private void customizedLoadClazz(String[] clazns) {
		dynclazns = new HashSet<String>();
		for (int i = 0; i < clazns.length; i++) {
			Class<?> loadedClass = loadDirectly(clazns[i]);
			if (loadedClass != null) {
				dynclazns.add(clazns[i]);
			}
		}
	}

	private Class<?> loadDirectly(String clazzName) { 
        Class<?> cls = null; 
        StringBuffer clazzFilePath = new StringBuffer(basedir); 
        // e.g. convert com.tools.AClass to com/tools/AClass.class
        String clazzFile = clazzName.replace('.', File.separatorChar) + ".class";
        clazzFilePath.append(File.separator + clazzFile); 
        cls = instantiateClass(clazzName, clazzFilePath.toString()); 
        return cls; 
    }
	
	private Class<?> instantiateClass(String clazzName, String clazzFilePath){ 
		File classF = new File(clazzFilePath); 
		System.out.println(classF.getAbsolutePath());
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(classF);
			byte[] raw = new byte[(int) classF.length()]; 
			fin.read(raw);
			return defineClass(clazzName, raw, 0, raw.length);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			FileUtils.closeInputStream(fin);
		}
    } 
    
	protected Class<?> loadClass(String clazzName, boolean resolve) 
            throws ClassNotFoundException { 
        Class<?> cls = null; 
        cls = findLoadedClass(clazzName); 
        if (cls == null) { // class not loaded 
        	// try to load the class itself
        	if (dynclazns.contains(clazzName)) {
        		cls = loadDirectly(clazzName);
        	} else { // delegate to the default loader
        		cls = getSystemClassLoader().loadClass(clazzName); 
        	}
        }
        if (cls == null) { 
            throw new ClassNotFoundException(clazzName); 
        }
        if (resolve) { 
            resolveClass(cls);
        }
        return cls; 
    } 

	
}
