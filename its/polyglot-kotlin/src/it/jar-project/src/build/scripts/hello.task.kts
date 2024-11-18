"""
------------------------------------------------------------------------

HELLO ${project.name}!!!

Folder:   ${basedir}
Script:   .${script.path.substringAfter(basedir.path)}
Session:  ${session}
Project:  ${project}
Log:      ${log}

------------------------------------------------------------------------
""".trimIndent().lines().forEach { log.info(it) }
