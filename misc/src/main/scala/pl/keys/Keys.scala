package pl.keys

import java.awt.event.KeyEvent
import java.util.Date

object Keys extends App {
  var state = true
  while(true) {
//    java.awt.Toolkit.getDefaultToolkit.setLockingKeyState(KeyEvent.VK_SCROLL_LOCK, state)
    println(new Date())
    Thread.sleep(1000)
    java.awt.Toolkit.getDefaultToolkit.setLockingKeyState(KeyEvent.VK_CAPS_LOCK, state)
//    Thread.sleep(500)
//    println(s"""java.awt.Toolkit.getDefaultToolkit.getLockingKeyState(KeyEvent.VK_CAPS_LOCK) = """ + java.awt.Toolkit.getDefaultToolkit.getLockingKeyState(KeyEvent.VK_CAPS_LOCK))
//    println(s"""java.awt.Toolkit.getDefaultToolkit.getLockingKeyState(KeyEvent.VK_SCROLL_LOCK) = """ + java.awt.Toolkit.getDefaultToolkit.getLockingKeyState(KeyEvent.VK_SCROLL_LOCK))
    println(s"""state = ${state}""")
    state = !state
  }
}
