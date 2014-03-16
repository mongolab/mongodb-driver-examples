require 'mongoid'
 
class Person
  include Mongoid::Document
  field :first_name
  field :middle_initial
  field :last_name
  store_in session: "people"
end
