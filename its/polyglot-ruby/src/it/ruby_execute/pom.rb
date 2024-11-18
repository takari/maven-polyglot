project 'Execute Ruby Tasks' do

  id 'com.example:ruby-pom:1.0-SNAPSHOT'
  
  build do
    execute("first", :initialize) do |context|
      java.lang.System.out.println context.project.name
    end
    execute(:second, :initialize) do |context|
      pt = context.project
      java.lang.System.out.println "#{pt.group_id}:#{pt.artifact_id}:#{pt.version}:#{pt.packaging}" 
    end
    execute(:third, :initialize) do
      java.lang.System.out.println where 
    end
    phase :validate do
      execute(:forth) do |ctx|
        ctx.log.info 'hello world'
        ctx.log.error ( ctx.basedir.to_pathname == ctx.project.basedir.to_pathname ).to_s
        ctx.log.error ctx.project.build.directory.to_pathname
      end
    end
  end
  
  def where
    "#{self.inspect.sub(/:0x[0-9a-f]{8}/,'')}"
  end
end  
