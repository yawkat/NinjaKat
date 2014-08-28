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

obfuscator.remap({
    in: "test/target/test-1.0-SNAPSHOT.jar",
    path: [],
    out: "test/target/test-1.0-SNAPSHOT-o.jar",
    class_mapper: function(c) {
        return random_name_supplier();
    },
    method_mapper: function(m) {
        return random_name_supplier();
    },
    field_mapper: function(f) {
        return random_name_supplier();
    }
});
