log.info("Starting remapper...");

chars = [];
for (var i = 0xf000; i < 0xffff; i++) {
    chars.push(String.fromCharCode(i));
}

function random_name_supplier(of) {
    var i = 0;
    return function() {
        var res = ""
        var j = i;
        do {
            res += of[j % of.length];
            j = Math.floor(j / of.length);
        } while (j > 0);
        i++;
        return res;
    };
}

obfuscator.remap({
    in: "test/target/test-1.0-SNAPSHOT.jar",
    // path: [],
    out: "test/target/test-1.0-SNAPSHOT-map.jar",
    class_mapper: function(c) {
        return random_name_supplier(chars);
    },
    method_mapper: function(m) {
        return random_name_supplier(chars);
    },
    field_mapper: function(f) {
        return random_name_supplier(chars);
    }
});
obfuscator.rename_locals({
    in: "test/target/test-1.0-SNAPSHOT-map.jar",
    out: "test/target/test-1.0-SNAPSHOT-renamelocal.jar",
    mapper: function(c, f) {
        return random_name_supplier(["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"]);
    }
});
