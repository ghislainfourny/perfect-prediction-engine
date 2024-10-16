declare variable $path as anyURI external := anyURI("/tmp/labeled");

for $i in json-file($path)
let $path := serialize($i.PTE.path)
group by $path
order by $path ascending
return {"$path" : $path, "count" : count($i) }