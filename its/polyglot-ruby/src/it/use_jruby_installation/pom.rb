project 'Execute Ruby Tasks' do

  id 'com.example:ruby-pom:1.0-SNAPSHOT'
  
  build do
    execute("first", :initialize) do |context|
      java.lang.System.out.println "jruby version #{JRUBY_VERSION}"
    end
  end
  
end  
