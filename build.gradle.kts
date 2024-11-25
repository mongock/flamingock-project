import org.jreleaser.model.Active

plugins {
    `kotlin-dsl`
    `maven-publish`
    id("java-library")
    id("org.jreleaser") version "1.15.0"

}

allprojects {
    group = "io.flamingock"
    version = "0.0.4"

    apply(plugin = "org.jetbrains.kotlin.jvm")
}


subprojects {


    apply(plugin = "java-library")




    if(shouldBeReleased(project)) {

        java {
            withSourcesJar()
            withJavadocJar()
        }



        apply(plugin = "maven-publish")
        apply(plugin = "org.jreleaser")
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    groupId = project.group.toString()
                    artifactId = project.name

                    from(components["java"])

                    pom {
                        name.set(project.name)
                        description.set("Description should be here")
                        url.set("https://github.com/mongock/flamingock-project")
                        inceptionYear.set("2024")

                        licenses {
                            license {
                                name.set("Apache-2.0")
                                url.set("https://spdx.org/licenses/Apache-2.0.html")
                            }
                        }
                        developers {
                            developer {
                                id.set("dieppa")
                                name.set("Antonio Perez Dieppa")
                            }
                        }
                        scm {
                            connection.set("scm:git:https://github.com:mongock/flamingock-project.git")
                            developerConnection.set("scm:git:ssh://github.com:mongock/flamingock-project.git")
                            url.set("https://github.com/mongock/flamingock-project")
                        }
                    }
                }
            }

            repositories {
                maven {
                    url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
                }
            }
        }

        jreleaser {
            signing {
                active.set(Active.ALWAYS)
                armored = true
                enabled = true
                passphrase.set("Quetefollen69veces+")
                publicKey.set("-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
                        "\n" +
                        "mQGNBGc/F04BDADAiszBN8TO+UTnY2s5C00Y9nUHUBb/lzudyWzBGZXZV9nvwZ2d\n" +
                        "dNlUyqwf5miFdJjHNzrnm1uNi1ZeARvoCQ+mLLlBgSEW+KyYkqZdNcmg9vzRzySr\n" +
                        "UxaxLSl8YVwX4Ib2EvIcpGpbz1NjAT5ecyI+nNopHl7saAsJ7yupJtN+3bUFdPaM\n" +
                        "MGlZYqgaOP5D4cEF8+CayHpZfpplcAqzCAgzZwOZdQkjLHtcUxo1IvBUNXXAvHXE\n" +
                        "Fk0FgTWPoNxkMmsjgVAJwZBd5XY4rTdj4u/xYHamcyUOInX6LvUjKoi88KKQc9rE\n" +
                        "Ib2PkVvWShZCbNJzG5YnTCeIr3gUvEu7lMQAyDGrGlNza/vmJQ4VZYpMCPFyzj1K\n" +
                        "3PtliT3tzDk0H2lj2UaQ3ZLJZzv9gUOKRPwnoR/8Cu7n5NOkyXHZWiQPQaKBM27t\n" +
                        "hhjg66dMUta61f6VYNbHs3sXUB0M+7k1mtiPs/7XyXg8TBr5xs8T0hIMfkmpSTn/\n" +
                        "4hPzdtdxaLirja8AEQEAAbRWQW50b25pbyBQZXJleiBEaWVwcGEgKFRvIHJlbGVh\n" +
                        "c2UgTW9uZ29jay9mbGFpbmdvY2sgYXJ0ZWZhY3RzKSA8YXBlcmV6ZGllcHBhQGdt\n" +
                        "YWlsLmNvbT6JAdEEEwEIADsWIQQohGcwOa3+A4W64GTkIwjYfXUJAwUCZz8XTgIb\n" +
                        "AwULCQgHAgIiAgYVCgkICwIEFgIDAQIeBwIXgAAKCRDkIwjYfXUJA+EADACP4zcv\n" +
                        "PT8tIUCH5HYjLuV8cpzbg7S8uun0ipygczHscD5MI6f0WqS206SRPX0Vh7nCBY0w\n" +
                        "eVjuvXt64J0mzfcwkaI7sOSVKRyqORl5Bj3Okej5MjCk4tzSV42aAc9cHveNsX11\n" +
                        "2ntC4zWCmWENOgr4dtwMD9Xfkd166DW2TAgikG9MIVeacVNyxS7GyxPcjwFDN6/f\n" +
                        "PUbCCb3oVen1xP6b+rru/fNaevFf3S33kFnxmyTdIORBpm3AE4k1MKFFmpsVDYV3\n" +
                        "zXc+xyLBjSUYy8CC8tl9v/9qT/bMIQg2ZacoGfGY1fBrv54tVT5Qx2rMYEJJ62tE\n" +
                        "zb8VtaLCcb7HVn2vA0iYyWrrVwFEbGELecCDHtSrWnt2qz0D1qHSwzb7sd5LqP41\n" +
                        "hyHRu1q0/S6Z8FjAbM3lg6ep/pzuJ8Otbnom+/BeLxwaJ1G4K994DumcuAQv5XT7\n" +
                        "l/XoPKyI1FZO97Dfr44HIKag786XW7IjRWfBGgda1eOfjiKLMGxcdrTprLy5AY0E\n" +
                        "Zz8XTgEMAOWElqpQBRnInOmaiL1k7ff3pjxsX5aJf1ETJvw5V6NckXvugamziHKY\n" +
                        "F3X/VzqVNpV2iJv3WZRXJjCFb1x4vCeNFIFJetAPqZk435t/KuQE08Gvz3OfBdFD\n" +
                        "Q5kJHKb1FeksXojVqQvcE7trVirXhNI6ByBvgVWmmY4oS4cLOWF25gIFT5yNx7hH\n" +
                        "AC7vDmF9Ik9jgaajkcuAQpJvwzn5TGgpuO8NvEdEuZPwUdFnLxloAODaIl1tBgVH\n" +
                        "SgVYCafSlYogdmO/ghIxt9AOblmFHnNsNSmMrswieqxN4RS5LXarEKJrWYKitnOY\n" +
                        "EDv4JT1mSZhMqmt2zdScUpfQRZdUOctcJB6x1jVstMkmbI87lgAI7dYexobpY926\n" +
                        "2LkbFyQrtDVu6785C12BGqr/0yETWpd2PoUSv29AGlNL6bz1isQ3XiO46qp8UTMj\n" +
                        "9Ob+9IZD4J5/8TyHsoYuTulmTP2X8+Xgr69iegKMgJ7ILv74m8I5fT5J3fJrprVL\n" +
                        "FPeiSfaIbQARAQABiQG2BBgBCAAgFiEEKIRnMDmt/gOFuuBk5CMI2H11CQMFAmc/\n" +
                        "F04CGwwACgkQ5CMI2H11CQOV2Av7B6vh4G1rS9//g5jy4WJtB6FtS0Sf9/dLFg7O\n" +
                        "DzkCbej1DR+FZzzOq1Oizr1cVEELTAFaF3ZrGBFiWF+ZTRPQmvgQ/HRTBMod0TMb\n" +
                        "U2SzY3VNAg69N5UmpSmtQ1hG+MaM32WqhZkuFxl5kT4mhoE8o90RVautfSsZAFng\n" +
                        "OANaH0Ncgf+AuUbiIh/sWuhTc71BVKLiZmLFurbNvZRcTHugLuzgVux5cETOwLk0\n" +
                        "SxrxFtQTTexgG9OQNxFRTTXGq9lHOdTP2J6nNeG3S/sTexKjmuK7lMcIhmxDc77A\n" +
                        "GPQnh2Q0YK1TaX8EReMhIVdTWwtxfq/TjRgT4piBCI03GAvZilhyM5CY2KgvsKq8\n" +
                        "hA5whMJVQWqVZ5H+obIf5UpeeQFGblMyHnHd2sh0AkQtF+Tlwmr+kAxp+XUQrl/9\n" +
                        "6KMoXyNYXUaryakzsGGNdTiDUEK/7YyjtfMiQ2+vfTPGtSlgHSWbjepTBTQ/MBk7\n" +
                        "2P687dlae0sg6MuuBDmOX+5coAGI\n" +
                        "=EFWj\n" +
                        "-----END PGP PUBLIC KEY BLOCK-----")
                secretKey.set("-----BEGIN PGP PRIVATE KEY BLOCK-----\n" +
                        "\n" +
                        "lQWGBGc/F04BDADAiszBN8TO+UTnY2s5C00Y9nUHUBb/lzudyWzBGZXZV9nvwZ2d\n" +
                        "dNlUyqwf5miFdJjHNzrnm1uNi1ZeARvoCQ+mLLlBgSEW+KyYkqZdNcmg9vzRzySr\n" +
                        "UxaxLSl8YVwX4Ib2EvIcpGpbz1NjAT5ecyI+nNopHl7saAsJ7yupJtN+3bUFdPaM\n" +
                        "MGlZYqgaOP5D4cEF8+CayHpZfpplcAqzCAgzZwOZdQkjLHtcUxo1IvBUNXXAvHXE\n" +
                        "Fk0FgTWPoNxkMmsjgVAJwZBd5XY4rTdj4u/xYHamcyUOInX6LvUjKoi88KKQc9rE\n" +
                        "Ib2PkVvWShZCbNJzG5YnTCeIr3gUvEu7lMQAyDGrGlNza/vmJQ4VZYpMCPFyzj1K\n" +
                        "3PtliT3tzDk0H2lj2UaQ3ZLJZzv9gUOKRPwnoR/8Cu7n5NOkyXHZWiQPQaKBM27t\n" +
                        "hhjg66dMUta61f6VYNbHs3sXUB0M+7k1mtiPs/7XyXg8TBr5xs8T0hIMfkmpSTn/\n" +
                        "4hPzdtdxaLirja8AEQEAAf4HAwL3wF40akH/lPQIMy0O5BfdZKYecEMIPP75Utpu\n" +
                        "3LI2gFLS+P8oWlozNw044MIiphnJZ9Fz5XUI2wB7UGgj0ZZA/WquMqiX1OZR6YB9\n" +
                        "jEXNH7z5s/eBoQl6nee1lqS1JxDzGhoU5PTlscSsVBo9sD4BKsekjBs7gKfVCW8f\n" +
                        "oYc6e4RQz5MuQ+7C75WdETnzfEHqPR4N6VIhJqXd6fu2DipLAEWvETMoxJ8lMNxp\n" +
                        "pIA+2p4azgM97J3DQYpdatEnsE267K0G4LDVnuYzsoqyqOqcppKVqW25AcdkAkwL\n" +
                        "KaTPmc1adHPtajfSXXr11NvRymESClmtRIJSb/X2OsKYBO5wCc676AuGRNQ9zkhf\n" +
                        "hcONLyKBSlceyhkF7RDxBhDwQw+WJvSkch3Bd4PWKjtnwsPc9C2fSQapscPnFx3m\n" +
                        "eKSH47UjMonVmRBvoNFJ3gpSe97+1GzXoVYRqZnUaPdZx5EXPcsxLVO2rUBkp9tj\n" +
                        "8ytcqaidgqh9pbnkqPLBQbrk+olxCHu1m72bA4DcuKLcRa3yaXQnN8wSvUi0WeAW\n" +
                        "Gc1X4wU8ED/DlcE6QhZgub6nRLzlh2nciVsaR3JlAE/bHWxTGBaqGpxc+Pem0Oty\n" +
                        "TNnWmZmqnOnv/j4AIzMz9bEW749Vo5h3TjQbMgYt/5M8onTP/qvi9kysygApAC13\n" +
                        "WiVLsEfjjeyKYbUUCj7Ilf3hJvXx9Hk5FT0T12NnCeqT39HCHUYqDfOke9f0Ym6J\n" +
                        "RD9yXbF9CQd5+9BGH6HwTSolKtZy472Tw3EfGXYeU/WeLS+QuLJQZYBBmOkw4mb0\n" +
                        "1HgQXOpbedSZWgs+/9O85jUbXAAiP1lYuapa/DgeMJA+68BdR04ZYXYZvgLZjCgb\n" +
                        "dblS7PUL4BDrX+2sDpasfl2uRaJN/rZVEv7JZ/vXQdQPZdBvIDKgJYZoJq1erNVP\n" +
                        "2GVONMLNgJoNRYkg1NacVaL3xPpFgag2qh/mio8CnNT/PzfieJklnOyFNxooQIrB\n" +
                        "kUR2n/Xv5kMBZBqpj3UXxJAyCKUDE2Nf3J/0rRaf7D1wRLwvqVUcWaOKHLbMfIe0\n" +
                        "YGtGGR/1fPbGCM1+C3dryqdX025QOQDBcYtw3IkBjBMNsMjNeheHmBT1BVktbX1E\n" +
                        "3AGhIJO++QkNC1ZvYu7w81utRjKshkVJD0enonVH3eKNo4ufR65cMwfjWw2ad0LW\n" +
                        "nYOcM+QNBASaoiJ6QDDb2FMLse2p70Y+1DAyWZYhulIRAsOzyvW9zMetgpebknNT\n" +
                        "KrFRKzPl5INxZgLMkCsE2wHV0UzU69q3rX12DDjguytz4Qv3ZWFF0pZH9+P6forC\n" +
                        "GV58YdYk8DqAsojcE3vqegVT9EkLgMliAbRWQW50b25pbyBQZXJleiBEaWVwcGEg\n" +
                        "KFRvIHJlbGVhc2UgTW9uZ29jay9mbGFpbmdvY2sgYXJ0ZWZhY3RzKSA8YXBlcmV6\n" +
                        "ZGllcHBhQGdtYWlsLmNvbT6JAdEEEwEIADsWIQQohGcwOa3+A4W64GTkIwjYfXUJ\n" +
                        "AwUCZz8XTgIbAwULCQgHAgIiAgYVCgkICwIEFgIDAQIeBwIXgAAKCRDkIwjYfXUJ\n" +
                        "A+EADACP4zcvPT8tIUCH5HYjLuV8cpzbg7S8uun0ipygczHscD5MI6f0WqS206SR\n" +
                        "PX0Vh7nCBY0weVjuvXt64J0mzfcwkaI7sOSVKRyqORl5Bj3Okej5MjCk4tzSV42a\n" +
                        "Ac9cHveNsX112ntC4zWCmWENOgr4dtwMD9Xfkd166DW2TAgikG9MIVeacVNyxS7G\n" +
                        "yxPcjwFDN6/fPUbCCb3oVen1xP6b+rru/fNaevFf3S33kFnxmyTdIORBpm3AE4k1\n" +
                        "MKFFmpsVDYV3zXc+xyLBjSUYy8CC8tl9v/9qT/bMIQg2ZacoGfGY1fBrv54tVT5Q\n" +
                        "x2rMYEJJ62tEzb8VtaLCcb7HVn2vA0iYyWrrVwFEbGELecCDHtSrWnt2qz0D1qHS\n" +
                        "wzb7sd5LqP41hyHRu1q0/S6Z8FjAbM3lg6ep/pzuJ8Otbnom+/BeLxwaJ1G4K994\n" +
                        "DumcuAQv5XT7l/XoPKyI1FZO97Dfr44HIKag786XW7IjRWfBGgda1eOfjiKLMGxc\n" +
                        "drTprLydBYYEZz8XTgEMAOWElqpQBRnInOmaiL1k7ff3pjxsX5aJf1ETJvw5V6Nc\n" +
                        "kXvugamziHKYF3X/VzqVNpV2iJv3WZRXJjCFb1x4vCeNFIFJetAPqZk435t/KuQE\n" +
                        "08Gvz3OfBdFDQ5kJHKb1FeksXojVqQvcE7trVirXhNI6ByBvgVWmmY4oS4cLOWF2\n" +
                        "5gIFT5yNx7hHAC7vDmF9Ik9jgaajkcuAQpJvwzn5TGgpuO8NvEdEuZPwUdFnLxlo\n" +
                        "AODaIl1tBgVHSgVYCafSlYogdmO/ghIxt9AOblmFHnNsNSmMrswieqxN4RS5LXar\n" +
                        "EKJrWYKitnOYEDv4JT1mSZhMqmt2zdScUpfQRZdUOctcJB6x1jVstMkmbI87lgAI\n" +
                        "7dYexobpY9262LkbFyQrtDVu6785C12BGqr/0yETWpd2PoUSv29AGlNL6bz1isQ3\n" +
                        "XiO46qp8UTMj9Ob+9IZD4J5/8TyHsoYuTulmTP2X8+Xgr69iegKMgJ7ILv74m8I5\n" +
                        "fT5J3fJrprVLFPeiSfaIbQARAQAB/gcDAgNsDLX2hYfs9KLQPMJe0LNmzpiQOFwt\n" +
                        "FVgHH8cDy7ArOOK6lw7YZcxFXBBbvXGQ5KMru1NJnGvsLsV6SALbOAtd76FPO/kD\n" +
                        "tZAKIl+dEEU2Jxq8dQbMQY8wFfpYsIMmNBNKcZ4s7lGuK2oxEy4AID08E0gTOizi\n" +
                        "KjX6XZjWVwN+HK8W8xzRBcS7fozfOLPbKDWWjhAo7pwNs7uS7+B+guDgKhuAS0N6\n" +
                        "38PmzTWiD0cIfx3Ps4DtJ2HTr3cT6Yufc6bvOs7jUiCsRsJpSrrMFtdEt5ztZhmO\n" +
                        "A3hJRSy+Gz81EXcIA3rX1SSX0RGTAjBRqqsgjvgM6UjEPBZznmC3ipe5fg3BVi+H\n" +
                        "dSctn2hBm0Q/AwttTiAH+nD+uEspS21q6IsbeVVuCsaMkaYP1YQQIn6qUWc3WojU\n" +
                        "faenW7LqoJLjaAze2XUM6X72aNR4KFiuzZ34WBf0SIbrpn+iN3ZtsnULWRl7Ccyk\n" +
                        "mL3L3Wp/AXyX8LG4BpdNYE2sSYSqaFq+Ksl9zIr5zVHq3hBR4myBxXbFV8K1rdID\n" +
                        "wmWXYACIE7u6owE0APU9J31ZhLz6aVXxLnhsgyzhBrkcQr51rYxsZz5/9w4JzUVo\n" +
                        "To3150A3gni3R57/cI2US/j4dzLTF6Wx0894MhGe6DHv6+1sUP43UYAxWF5hrTRH\n" +
                        "zvkHzBvFB29qKpJZPrymcrkKKUAxxiCIyo0CflK4wY3hN3Bz8WBRJG3XHriBvCqx\n" +
                        "6RhK9UzOUUZB2sBx6K7fm7VjOA6UPXkfrVs8lLYVpLPNtG217r01YdoQJHrHJntI\n" +
                        "2Cj9rm8lEHFCkX/krreumnLv7fzam/p8TK4wlX+4cbPwMYTROs3lQWF0otYmFHCe\n" +
                        "uFKaIhGAmFDUZp3HorKb7xrkWn7IngedCGq3HZjVnJy1MrHXUDrlwvHsug8iszqD\n" +
                        "9V4VK7rLsnolzQjjA9rBtm1lONJRs982wS2hUrZx4C9QTdahZC8T7YMI1GtudVA6\n" +
                        "L2T/g0tyL4fsQ/3t14w49z0TXg7e94FZTfFoUuKowpoAJ7Acbm+iu30XDT0Ezhbe\n" +
                        "yuc3et5SDek2/H8adIl5mLn31FgN+5SR83QsDtNESc7TBUnHtFnhczFDT5IiKmLl\n" +
                        "AJvi0PAHu05XhmrH+Lo/5ImDagiXKuQh5pBl8Byy6s6dgpfbgIFm3iHNjf2xbYBC\n" +
                        "nOsztG8m6SG8IATFVZXaNBoKfge5O7V/iK5ivQavhftHGkuBx4+ihZZJw2bQkHn8\n" +
                        "JczM5GVkTQmjNuQKeObBaG4CuiX9NN+785ygEAb0IO9X3bQv92iqgWywfi/q1qnJ\n" +
                        "k2ktepiJoHo/W4DrcPbtNUqy0l2k9BRoa0rW0Dt8iQG2BBgBCAAgFiEEKIRnMDmt\n" +
                        "/gOFuuBk5CMI2H11CQMFAmc/F04CGwwACgkQ5CMI2H11CQOV2Av7B6vh4G1rS9//\n" +
                        "g5jy4WJtB6FtS0Sf9/dLFg7ODzkCbej1DR+FZzzOq1Oizr1cVEELTAFaF3ZrGBFi\n" +
                        "WF+ZTRPQmvgQ/HRTBMod0TMbU2SzY3VNAg69N5UmpSmtQ1hG+MaM32WqhZkuFxl5\n" +
                        "kT4mhoE8o90RVautfSsZAFngOANaH0Ncgf+AuUbiIh/sWuhTc71BVKLiZmLFurbN\n" +
                        "vZRcTHugLuzgVux5cETOwLk0SxrxFtQTTexgG9OQNxFRTTXGq9lHOdTP2J6nNeG3\n" +
                        "S/sTexKjmuK7lMcIhmxDc77AGPQnh2Q0YK1TaX8EReMhIVdTWwtxfq/TjRgT4piB\n" +
                        "CI03GAvZilhyM5CY2KgvsKq8hA5whMJVQWqVZ5H+obIf5UpeeQFGblMyHnHd2sh0\n" +
                        "AkQtF+Tlwmr+kAxp+XUQrl/96KMoXyNYXUaryakzsGGNdTiDUEK/7YyjtfMiQ2+v\n" +
                        "fTPGtSlgHSWbjepTBTQ/MBk72P687dlae0sg6MuuBDmOX+5coAGI\n" +
                        "=vO5L\n" +
                        "-----END PGP PRIVATE KEY BLOCK-----")
            }

            gitRootSearch.set(true)

            deploy {
                maven {
                    mavenCentral {

                        create("sonatype") {
                            active.set(Active.ALWAYS)
                            url.set("https://central.sonatype.com/api/v1/publisher")
                            stagingRepository("build/staging-deploy")
                        }


                    }
                }
            }
        }
    }


    repositories {
        mavenCentral()
        mavenLocal()
    }


    val implementation by configurations
    val testImplementation by configurations
    val testRuntimeOnly by configurations

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("org.slf4j", "slf4j-api", "2.0.6")

        testImplementation("org.slf4j:slf4j-simple:2.0.6")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")

        testImplementation("org.mockito:mockito-core:4.11.0")
        testImplementation("org.mockito:mockito-junit-jupiter:4.11.0")
        testImplementation("org.mockito:mockito-inline:4.11.0")

    }
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            events(
                    org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                    org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                    org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                    org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
            )
        }
    }


    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }
}

fun shouldBeReleased(project: Project) : Boolean {
    return project.name == "utils"
}
