<messageML>
    <form id="trade-table">
        <h2>Select team members to help resolve trade breaks:</h2>
        <person-selector name="person-selector" required="true" placeholder="Required"/>
        <table>
            <thead>
            <tr>
                <td>Counterparty</td>
                <td>Status</td>
                <td>State</td>
                <td>Description</td>
                <td>Portfolio</td>
                <td>Price</td>
                <td>Quantity</td>
                <td>Select</td>
            </tr>
            </thead>
            <tbody>
            <#assign trades = trades>
                <#list trades as trade>
                    <tr>
                        <td>${trade.counterparty}</td>
                        <td>${trade.status}</td>
                        <td>${trade.state}</td>
                        <td>${trade.description}</td>
                        <td>${trade.portfolio}</td>
                        <td>${trade.price}</td>
                        <td>${trade.quantity}</td>
                        <td>${trade.transaction}</td>
                        <td>
                            <button name="resolve-${trade.id}" type="action">RESOLVE</button>
                        </td>
                    </tr>
                </#list>
            </tbody>
        </table>
    </form>
</messageML>

