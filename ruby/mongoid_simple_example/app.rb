require 'rubygems'
require 'sinatra'
require 'mongoid'
require_relative 'person.rb'

Mongoid.load!("mongoid.yml", :production)

get '/' do
  person = Person.new(:first_name => "Ludwig", :last_name => "Beethoven")
  person.save

  "Hello, I am #{person.first_name} #{person.last_name}"
end
