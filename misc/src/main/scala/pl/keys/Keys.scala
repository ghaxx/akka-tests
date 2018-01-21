package pl.keys

import java.awt.{AWTError, GraphicsEnvironment, Toolkit}
import java.awt.event.KeyEvent
import java.security.PrivilegedAction
import java.util.Date

import sun.awt.HeadlessToolkit

object Keys extends App {
  var state = true

  var toolkit: Toolkit = null

  def getDefaultToolkit = {
    if (toolkit == null) {
      java.security.AccessController.doPrivileged(new PrivilegedAction[Void]() {
        override def run: Void = {
          var cls: Class[_] = null
          val nm = System.getProperty("awt.toolkit")
          try
            cls = Class.forName(nm)
          catch {
            case e: ClassNotFoundException =>
              val cl = ClassLoader.getSystemClassLoader
              if (cl != null) try
                cls = cl.loadClass(nm)
              catch {
                case ignored: ClassNotFoundException =>
                  throw new AWTError("Toolkit not found: " + nm)
              }
          }
          try
              if (cls != null) {
                toolkit = cls.newInstance.asInstanceOf[Toolkit]
                if (GraphicsEnvironment.isHeadless) toolkit = new HeadlessToolkit(toolkit)
              }
          catch {
            case ignored: InstantiationException =>
              throw new AWTError("Could not instantiate Toolkit: " + nm)
            case ignored: IllegalAccessException =>
              throw new AWTError("Could not access Toolkit: " + nm)
          }
          null
        }
      })
    }
    toolkit
  }

  while(true) {
//    java.awt.Toolkit.getDefaultToolkit.setLockingKeyState(KeyEvent.VK_SCROLL_LOCK, state)
//    println(new Date())
    Thread.sleep(100)
//    java.awt.Toolkit.getDefaultToolkit.setLockingKeyState(KeyEvent.VK_CAPS_LOCK, state)
//    Thread.sleep(500)
    print("\r" + new Date() + s"""java.awt.Toolkit.getDefaultToolkit.getLockingKeyState(KeyEvent.VK_CAPS_LOCK) = """ + getDefaultToolkit.getLockingKeyState(KeyEvent.VK_CAPS_LOCK))
//    println(s"""java.awt.Toolkit.getDefaultToolkit.getLockingKeyState(KeyEvent.VK_SCROLL_LOCK) = """ + java.awt.Toolkit.getDefaultToolkit.getLockingKeyState(KeyEvent.VK_SCROLL_LOCK))
//    println(s"""state = ${state}""")
    state = !state
  }
}
