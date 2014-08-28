log.info("Starting remapper...");

chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVW"

function random_name_supplier() {
    var i = 0;
    return function() {
        var res = ""
        var j = i;
        do {
            res += chars.charAt(j % chars.length);
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
