log.info("Starting remapper...");

chars = [];
for (var i = 0xf000; i < 0xffff; i++) {
    chars.push(String.fromCharCode(i));
}

function random_name_supplier() {
    var i = 0;
    return function() {
        var res = ""
        var j = i;
        do {
            res += chars[j % chars.length];
            j = Math.floor(j / chars.length);
        } while (j > 0);
        i++;
        return res;
    };
}

obfuscator.remap(
    "test/target/test-1.0-SNAPSHOT.jar",
    [],
    "test/target/test-1.0-SNAPSHOT-o.jar",
    function(c) {
        return random_name_supplier();
    },
    function(m) {
        return random_name_supplier();
    },
    function(f) {
        return random_name_supplier();
    }
);
