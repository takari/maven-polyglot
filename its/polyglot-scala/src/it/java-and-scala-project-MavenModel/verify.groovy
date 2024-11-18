try {
    def file = new File(basedir, 'target/classes/ScalaClass.class')
    assert file.exists()

    def file2 = new File(basedir, 'target/classes/JavaClass.class')
    assert file2.exists()

    def file3 = new File(basedir, 'target/classes/JavaInterface.class')
    assert file3.exists()

    def file4 = new File(basedir, 'target/testJavaAndScala-1.0-SNAPSHOT.jar')
    assert file4.exists()

    return true

} catch (Throwable e) {
    e.printStackTrace()
    return false
}