
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class DualClassLoader extends ClassLoader {
    
    // not our real parents, but we try them too
    List<ClassLoader> fosterParents = new ArrayList<>();

    public DualClassLoader(ClassLoader parent) {
        super("DualClassLoader",parent);
    }
    
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        for (ClassLoader cl : fosterParents) {
            try {
                return cl.loadClass(name);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        return getParent().loadClass(name);
    }

    public void append(ClassLoader additional){
        fosterParents.add(additional);
    }
    
    //?
    //protected String findLibrary(String libname) {}
    
    @Override
    protected URL findResource(String name) {

        URL result = null;
        for (ClassLoader cl : fosterParents) {
                result = cl.getResource(name);
                if (result != null)
                return result;
        }
        return getParent().getResource(name);
    }
    
    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        List<URL> results = new ArrayList<>();

        for (ClassLoader cl : fosterParents) {
                results.addAll(Collections.list(cl.getResources(name)));
        }
        return Collections.enumeration(results);
    }
    
}
