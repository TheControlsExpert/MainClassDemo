package vrs_library;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class OpModeManager {

    private static OpModeManager manager;
    private HardwareMap hardwareMap = new HardwareMap();

    private OpModeManager() {}

   // public native boolean ReadyToStart();
   // public native boolean opModeIsActive();

    public static OpModeManager getInstance() {
        if (manager == null) {
            manager = new OpModeManager();
        }

        return manager;
    }


    public void run() {
        String currentOp = "Autonomous";
        //String currentOp = getChosenOpMode();
        Class<?> clazz = null;

        if (currentOp.equals("Teleop")) {
            try {
            clazz = findTeleopClasses();
            }

            catch(TeleopClassNotFoundError e) {
                e.printStackTrace();
            }
        }

        else {
            try {
            clazz = findAutonomousClasses();
            }

            catch(AutonomousClassNotFoundError e) {
                e.printStackTrace();
            }
        }
        
       
             if (clazz.isAssignableFrom(OpMode.class)) {
                //iterative op mode
              try {
                OpMode runnable = (OpMode) clazz.getDeclaredConstructor().newInstance();
                runnable.init();

                while(true) {
                    runnable.init_loop();
                   // if (ReadyToStart()) {
                        break;
                    //}

                }

                runnable.start();
                //while(opModeIsActive()) {
                    runnable.loop();
                    //
                  //  break;
                 //   

               // }
                runnable.stop();
                




              }

              catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
              }
            

                
            }

            else {
                //assuming its autonomous   
                try {    
                LinearOpMode runnable = (LinearOpMode) clazz.getDeclaredConstructor().newInstance();
                runnable.runOpMode();
                } 

                catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                  }

    
            }

    }

    

    public native String getChosenOpMode();

    public HardwareMap getHardwareMap() {
        return hardwareMap;
    }
 

public static Class<?> findAutonomousClasses() throws AutonomousClassNotFoundError {
    URL jarLocation = mainRunner.class.getProtectionDomain().getCodeSource().getLocation();
    try {
        File jarFile = new File(jarLocation.toURI());
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarLocation});
        List<Class<?>> classes = new ArrayList<>();
        

    try {JarFile jar = new JarFile(jarFile);
        String directoryPrefix = "TeamCode";
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            
            if (name.startsWith(directoryPrefix) && !entry.isDirectory()) {
                // Print just the file name, or the full path
                String className = name.replace("/", ".").replace(".class", "");
                System.out.println(className);
                try {
                Class<?> clazz = classLoader.loadClass(className);
                if (clazz.isAnnotationPresent(Autonomous.class)) {
                    System.out.println("found you! Autonomous is in" + className);
                    return clazz;


                }

                }

                catch(ClassNotFoundException e) {
                    System.out.println("no such class found :(");
                }
            }
        }

        throw new AutonomousClassNotFoundError();
    }

    catch(IOException e) {
        e.printStackTrace();
        throw new AutonomousClassNotFoundError();

    }
}

catch (URISyntaxException e) {
    e.printStackTrace();
    throw new AutonomousClassNotFoundError();
}




   

}

public static Class<?> findTeleopClasses() throws TeleopClassNotFoundError {
    URL jarLocation = mainRunner.class.getProtectionDomain().getCodeSource().getLocation();
    try {
        File jarFile = new File(jarLocation.toURI());
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarLocation});
        List<Class<?>> classes = new ArrayList<>();
        

    try {JarFile jar = new JarFile(jarFile);
        String directoryPrefix = "combined/TeamCode";
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            
            if (name.startsWith(directoryPrefix) && !entry.isDirectory()) {
                // Print just the file name, or the full path
                String className = name.replace("/", ".").replace(".class", "");
                System.out.println(className);
                try {
                Class<?> clazz = classLoader.loadClass(className);
                if (clazz.isAnnotationPresent(Teleop.class)) {
                    System.out.println("found you! Teleop is in" + className);
                    return clazz;


                }

                }

                catch(ClassNotFoundException e) {
                    System.out.println("no such class found :(");
                }
            }
        }

        throw new TeleopClassNotFoundError();
    }

    catch(IOException e) {
        e.printStackTrace();
        throw new TeleopClassNotFoundError();

    }
}

catch (URISyntaxException e) {
    e.printStackTrace();
    throw new TeleopClassNotFoundError();
}




   

}


public static class AutonomousClassNotFoundError extends Exception {
    public AutonomousClassNotFoundError() {
        super("Autonomous Class was not found. Check to make sure you used the @Autonomous annnotation");
    }
}
public static class TeleopClassNotFoundError extends Exception {
    public TeleopClassNotFoundError() {
        super("Teleop Class was not found. Check to make sure you used the @Autonomous annnotation");
    }
}

}
