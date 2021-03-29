module.exports = {
    "root": true,
    parser: '@typescript-eslint/parser',
    extends: [
        'plugin:@typescript-eslint/recommended',
        'plugin:prettier/recommended'
    ],
    "parserOptions": { "ecmaVersion": 6 }
    ,
    plugins: ['@typescript-eslint'],
    settings: {
        react: {
            version: 'detect'
        }
    },
    "rules": {
        // advised to turn off this rule:  https://github.com/eslint/eslint/issues/11464
        "no-useless-constructor": "off",
        "@typescript-eslint/no-useless-constructor": "error",
        "max-len": ["error", {"code": 100, "ignoreUrls": true}]
    },
    "overrides": [
        {
            "files": ["bin/*.ts", "lib/*.ts"],
            "excludedFiles": "lib/*.d.ts"
        }
    ]
}
