with import <nixpkgs> {}; {
  java-env = stdenv.mkDerivation {
    name = "java-env";
    JAVA_HOME = "${pkgs.jdk.home}";
    buildInputs = [
      openjdk8
    ];
  };

}
