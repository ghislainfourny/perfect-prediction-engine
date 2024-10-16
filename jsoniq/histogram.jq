declare variable $path as anyURI external := anyURI("/tmp/labeled");

declare	type local:histogram as { "path" : "string", "outcome" : "integer", "count" : "integer" };

validate type local:histogram* {

for $i in json-file($path)
let $path := serialize($i.PTE.path)
group by $path
order by $path ascending
count $position
return {"path" : $path, "outcome" : $position - 1, "count" : count($i) }

}
